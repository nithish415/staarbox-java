package com.example.demo.service;

import com.example.demo.entity.NotificationLog;
import com.example.demo.entity.UserDevice;
import com.example.demo.repo.NotificationLogRepository;
import com.example.demo.repo.UserDeviceRepository;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FCMService {

    private static final Logger logger = LoggerFactory.getLogger(FCMService.class);
    private static final int BATCH_SIZE = 500; // FCM limit

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * Send notification to a single device token.
     * Automatically handles token cleanup on failure.
     */
    @Transactional
    public void sendNotification(UserDevice device, String title, String body, String notificationType) {
        if (device == null || device.getDeviceToken() == null || device.getDeviceToken().isBlank()) {
            logger.warn("Skipping notification - invalid device");
            return;
        }

        if (!device.isActive()) {
            logger.debug("Skipping notification - device is inactive: {}", device.getId());
            return;
        }

        String token = device.getDeviceToken();
        NotificationLog log = new NotificationLog(device.getUserId(), token, notificationType, title, body);

        try {
            Message message = buildMessage(token, title, body, device.getPlatform());
            String messageId = FirebaseMessaging.getInstance().send(message);
            
            // Success
            device.markAsUsed();
            userDeviceRepository.save(device);
            
            log.setSuccess(true);
            log.setFcmMessageId(messageId);
            notificationLogRepository.save(log);
            
            logger.debug("Notification sent successfully | userId={} | token={} | messageId={}", 
                        device.getUserId(), maskToken(token), messageId);

        } catch (FirebaseMessagingException e) {
            handleFirebaseError(device, log, e);
        } catch (Exception e) {
            logger.error("Unexpected error sending notification: {}", e.getMessage(), e);
            log.setSuccess(false);
            log.setErrorMessage("Unexpected error: " + e.getMessage());
            notificationLogRepository.save(log);
        }
    }

    /**
     * Send same notification to a list of devices in batches.
     * More efficient than individual sends.
     */
    @Transactional
    public void sendBatch(List<UserDevice> devices, String title, String body, String notificationType) {
        if (devices == null || devices.isEmpty()) {
            logger.warn("No devices to send batch notification to");
            return;
        }

        // Filter only active devices
        List<UserDevice> activeDevices = devices.stream()
                .filter(UserDevice::isActive)
                .collect(Collectors.toList());

        if (activeDevices.isEmpty()) {
            logger.warn("No active devices found in batch");
            return;
        }

        logger.info("Sending batch notification | type={} | totalDevices={} | activeDevices={}", 
                   notificationType, devices.size(), activeDevices.size());

        // Split into batches of 500 (FCM limit)
        for (int i = 0; i < activeDevices.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, activeDevices.size());
            List<UserDevice> batch = activeDevices.subList(i, end);
            sendBatchInternal(batch, title, body, notificationType);
        }
    }

    private void sendBatchInternal(List<UserDevice> devices, String title, String body, String notificationType) {
        List<String> tokens = devices.stream()
                .map(UserDevice::getDeviceToken)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            return;
        }

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(1)
                                    .build())
                            .build())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            
            logger.info("Batch notification sent | success={} | failed={} | total={}", 
                       response.getSuccessCount(), response.getFailureCount(), tokens.size());

            // Process individual responses for cleanup
            processBatchResponse(devices, response, title, body, notificationType);

        } catch (FirebaseMessagingException e) {
            logger.error("Batch FCM error: {}", e.getMessage(), e);
            
            // Log all as failed
            for (UserDevice device : devices) {
                NotificationLog log = new NotificationLog(device.getUserId(), 
                        device.getDeviceToken(), notificationType, title, body);
                log.setSuccess(false);
                log.setErrorMessage("Batch error: " + e.getMessage());
                notificationLogRepository.save(log);
            }
        }
    }

    private void processBatchResponse(List<UserDevice> devices, BatchResponse response, 
                                     String title, String body, String notificationType) {
        List<SendResponse> responses = response.getResponses();
        
        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);
            UserDevice device = devices.get(i);
            
            NotificationLog log = new NotificationLog(device.getUserId(), 
                    device.getDeviceToken(), notificationType, title, body);

            if (sendResponse.isSuccessful()) {
                device.markAsUsed();
                userDeviceRepository.save(device);
                
                log.setSuccess(true);
                log.setFcmMessageId(sendResponse.getMessageId());
            } else {
                FirebaseMessagingException exception = sendResponse.getException();
                handleFirebaseError(device, log, exception);
            }
            
            notificationLogRepository.save(log);
        }
    }

    private void handleFirebaseError(UserDevice device, NotificationLog log, FirebaseMessagingException e) {
        String errorCode = e.getErrorCode().toString();
        String errorMessage = e.getMessage();
        
        log.setSuccess(false);
        log.setErrorMessage(errorCode + ": " + errorMessage);
        
        // Handle specific error codes
        switch (errorCode) {
            case "NOT_FOUND":
            case "UNREGISTERED":
            case "INVALID_ARGUMENT":
                // Token is invalid - deactivate it
                logger.warn("Deactivating invalid device token | userId={} | error={}", 
                           device.getUserId(), errorCode);
                device.markAsFailed(errorCode);
                userDeviceRepository.save(device);
                break;
                
            case "SENDER_ID_MISMATCH":
                logger.error("FCM Sender ID mismatch - check Firebase configuration!");
                device.markAsFailed(errorCode);
                userDeviceRepository.save(device);
                break;
                
            case "QUOTA_EXCEEDED":
            case "UNAVAILABLE":
                logger.warn("FCM temporary error | error={} | Will retry later", errorCode);
                break;
                
            default:
                logger.error("FCM error | userId={} | error={} | message={}", 
                            device.getUserId(), errorCode, errorMessage);
                device.markAsFailed(errorCode);
                userDeviceRepository.save(device);
        }
    }

    private Message buildMessage(String token, String title, String body, String platform) {
        Message.Builder builder = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        // Platform-specific configuration
        if ("android".equalsIgnoreCase(platform)) {
            builder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                            .build())
                    .build());
        } else if ("ios".equalsIgnoreCase(platform)) {
            builder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .setBadge(1)
                            .setContentAvailable(true)
                            .build())
                    .build());
        }

        return builder.build();
    }

    /**
     * Send notification directly to a raw FCM token (no DB lookup needed).
     * Returns the FCM message ID on success, or throws on failure.
     */
    @Transactional
    public String sendToToken(String token, String title, String body, String platform) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Device token must not be blank");
        }

        NotificationLog log = new NotificationLog(null, token, "direct", title, body);
        try {
            Message message = buildMessage(token, title, body, platform);
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.setSuccess(true);
            log.setFcmMessageId(messageId);
            notificationLogRepository.save(log);
            logger.info("Direct token notification sent | token={} | messageId={}", maskToken(token), messageId);
            return messageId;
        } catch (FirebaseMessagingException e) {
            log.setSuccess(false);
            log.setErrorMessage(e.getErrorCode() + ": " + e.getMessage());
            notificationLogRepository.save(log);
            logger.error("FCM error sending to token | error={} | message={}", e.getErrorCode(), e.getMessage());
            throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage(), e);
        }
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...";
    }
}
