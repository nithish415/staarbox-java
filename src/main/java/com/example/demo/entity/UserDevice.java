package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_device", 
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_device_token", columnList = "deviceToken"),
           @Index(name = "idx_is_active", columnList = "isActive")
       })
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "deviceToken", nullable = false, length = 500)
    private String deviceToken;

    @Column(name = "platform", length = 10)
    private String platform; // "android" or "ios"

    @Column(name = "appVersion", length = 20)
    private String appVersion;

    @Column(name = "isActive", nullable = false)
    private boolean isActive = true;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "lastUsedAt")
    private LocalDateTime lastUsedAt;

    @Column(name = "failureCount")
    private int failureCount = 0;

    @Column(name = "lastFailureReason", length = 255)
    private String lastFailureReason;

    public UserDevice() {
        this.createdAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
    }

    public UserDevice(Long userId, String deviceToken, String platform, String appVersion) {
        this();
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.platform = platform;
        this.appVersion = appVersion;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastUsedAt == null) {
            lastUsedAt = LocalDateTime.now();
        }
    }

    public void markAsUsed() {
        this.lastUsedAt = LocalDateTime.now();
        this.failureCount = 0;
        this.lastFailureReason = null;
    }

    public void markAsFailed(String reason) {
        this.failureCount++;
        this.lastFailureReason = reason;
        // Auto-deactivate after 3 consecutive failures
        if (this.failureCount >= 3) {
            this.isActive = false;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public String getLastFailureReason() {
        return lastFailureReason;
    }

    public void setLastFailureReason(String lastFailureReason) {
        this.lastFailureReason = lastFailureReason;
    }
}
