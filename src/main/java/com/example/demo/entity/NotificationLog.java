package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_log",
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_sent_at", columnList = "sentAt"),
           @Index(name = "idx_notification_type", columnList = "notificationType")
       })
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "deviceToken", length = 500)
    private String deviceToken;

    @Column(name = "notificationType", length = 50, nullable = false)
    private String notificationType;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "errorMessage", length = 500)
    private String errorMessage;

    @Column(name = "fcmMessageId", length = 255)
    private String fcmMessageId;

    @Column(name = "sentAt", nullable = false)
    private LocalDateTime sentAt;

    public NotificationLog() {
        this.sentAt = LocalDateTime.now();
    }

    public NotificationLog(Long userId, String deviceToken, String notificationType, 
                          String title, String message) {
        this();
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
    }

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFcmMessageId() {
        return fcmMessageId;
    }

    public void setFcmMessageId(String fcmMessageId) {
        this.fcmMessageId = fcmMessageId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
