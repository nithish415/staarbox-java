package com.example.demo.rest;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.RegisterDeviceRequest;
import com.example.demo.dto.SendNotificationRequest;
import com.example.demo.entity.UserDevice;
import com.example.demo.entity.UserNotificationPreference;
import com.example.demo.service.NotificationService;
import com.google.firebase.FirebaseApp;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API for device token registration and notification management.
 *
 * POST /api/notification/register              → Register mobile FCM token
 * POST /api/notification/deactivate            → Deactivate device token
 * POST /api/notification/send/{userId}         → Send to specific user (admin only)
 * POST /api/notification/send-all              → Broadcast to all (admin only)
 * GET  /api/notification/preferences/{userId}  → Get user preferences
 * PUT  /api/notification/preferences/{userId}  → Update user preferences
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private com.example.demo.service.FCMService fcmService;

    /**
     * Register device token for push notifications.
     * Called by mobile app after login.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerDevice(
            @Valid @RequestBody RegisterDeviceRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null 
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Invalid request data";
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(errorMsg));
        }

        try {
            UserDevice device = notificationService.registerDevice(
                    request.getUserId(),
                    request.getDeviceToken(),
                    request.getPlatform(),
                    request.getAppVersion()
            );

            Map<String, Object> data = new HashMap<>();
            data.put("deviceId", device.getId());
            data.put("userId", device.getUserId());
            data.put("platform", device.getPlatform());
            data.put("isActive", device.isActive());

            return ResponseEntity.ok(
                    ApiResponse.success("Device registered successfully", data)
            );

        } catch (Exception e) {
            logger.error("Error registering device: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to register device"));
        }
    }

    /**
     * Deactivate a device token (user logs out or uninstalls app).
     */
    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateDevice(
            @RequestBody Map<String, Object> request) {
        
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String deviceToken = request.get("deviceToken").toString();

            notificationService.deactivateDevice(userId, deviceToken);

            return ResponseEntity.ok(
                    ApiResponse.success("Device deactivated successfully")
            );

        } catch (Exception e) {
            logger.error("Error deactivating device: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate device"));
        }
    }

    /**
     * Send notification to a specific user.
     * TODO: Add admin authentication check
     */
    @PostMapping("/send/{userId}")
    public ResponseEntity<ApiResponse<Void>> sendToUser(
            @PathVariable Long userId,
            @Valid @RequestBody SendNotificationRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null 
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Invalid request data";
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(errorMsg));
        }

        try {
            notificationService.sendToUser(
                    userId,
                    request.getTitle(),
                    request.getMessage(),
                    "custom"
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Notification sent successfully")
            );

        } catch (Exception e) {
            logger.error("Error sending notification: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send notification"));
        }
    }

    /**
     * Broadcast notification to all users.
     * TODO: Add admin authentication check
     */
    @PostMapping("/send-all")
    public ResponseEntity<ApiResponse<Void>> sendToAll(
            @Valid @RequestBody SendNotificationRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null 
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Invalid request data";
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(errorMsg));
        }

        try {
            notificationService.sendToAllUsers(
                    request.getTitle(),
                    request.getMessage(),
                    "broadcast"
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Broadcast notification sent successfully")
            );

        } catch (Exception e) {
            logger.error("Error broadcasting notification: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to broadcast notification"));
        }
    }

    /**
     * Get user's notification preferences.
     */
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<UserNotificationPreference>> getPreferences(
            @PathVariable Long userId) {
        
        try {
            UserNotificationPreference preferences = 
                    notificationService.getOrCreatePreferences(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("Preferences retrieved successfully", preferences)
            );

        } catch (Exception e) {
            logger.error("Error getting preferences: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get preferences"));
        }
    }

    /**
     * Update user's notification preferences.
     */
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<UserNotificationPreference>> updatePreferences(
            @PathVariable Long userId,
            @RequestBody UserNotificationPreference preferences) {
        
        try {
            UserNotificationPreference updated = 
                    notificationService.updatePreferences(userId, preferences);

            return ResponseEntity.ok(
                    ApiResponse.success("Preferences updated successfully", updated)
            );

        } catch (Exception e) {
            logger.error("Error updating preferences: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update preferences"));
        }
    }

    /**
     * Send a push notification directly to a specific FCM token.
     * Use this when you already have the token and want immediate delivery.
     *
     * POST /api/notification/send-to-token
     * Body: { "deviceToken": "...", "title": "...", "message": "...", "platform": "android|ios" }
     */
    @PostMapping("/send-to-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendToToken(
            @RequestBody Map<String, String> request) {

        String deviceToken = request.get("deviceToken");
        String title       = request.get("title");
        String message     = request.get("message");
        String platform    = request.getOrDefault("platform", "android");

        if (deviceToken == null || deviceToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("deviceToken is required"));
        }
        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("title is required"));
        }
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("message is required"));
        }

        try {
            String messageId = fcmService.sendToToken(deviceToken, title, message, platform);

            Map<String, Object> data = new HashMap<>();
            data.put("messageId", messageId);
            data.put("token", deviceToken.length() > 10 ? deviceToken.substring(0, 10) + "..." : deviceToken);
            data.put("platform", platform);

            return ResponseEntity.ok(ApiResponse.success("Push notification sent successfully", data));

        } catch (Exception e) {
            logger.error("Error sending to token: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("FCM error: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint — no auth required.
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "notification");

        boolean firebaseReady = !FirebaseApp.getApps().isEmpty();
        status.put("firebase", firebaseReady ? "INITIALIZED" : "NOT_INITIALIZED");
        if (firebaseReady) {
            status.put("firebaseProject", FirebaseApp.getInstance().getOptions().getProjectId() != null
                    ? FirebaseApp.getInstance().getOptions().getProjectId() : "unknown");
        }

        return ResponseEntity.ok(ApiResponse.success("Service is healthy", status));
    }
}
