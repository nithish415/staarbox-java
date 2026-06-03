package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_notification_preference",
       indexes = @Index(name = "idx_user_id", columnList = "userId"))
public class UserNotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "userId", nullable = false, unique = true)
    private Long userId;

    @Column(name = "notificationsEnabled", nullable = false)
    private boolean notificationsEnabled = true;

    @Column(name = "foodReadyEnabled", nullable = false)
    private boolean foodReadyEnabled = true;

    @Column(name = "morningDeliveryEnabled", nullable = false)
    private boolean morningDeliveryEnabled = true;

    @Column(name = "dispatchEnabled", nullable = false)
    private boolean dispatchEnabled = true;

    @Column(name = "breakfastTipEnabled", nullable = false)
    private boolean breakfastTipEnabled = true;

    @Column(name = "customizationEnabled", nullable = false)
    private boolean customizationEnabled = true;

    @Column(name = "renewalReminderEnabled", nullable = false)
    private boolean renewalReminderEnabled = true;

    @Column(name = "marketingEnabled", nullable = false)
    private boolean marketingEnabled = true;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    public UserNotificationPreference() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UserNotificationPreference(Long userId) {
        this();
        this.userId = userId;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isFoodReadyEnabled() {
        return foodReadyEnabled;
    }

    public void setFoodReadyEnabled(boolean foodReadyEnabled) {
        this.foodReadyEnabled = foodReadyEnabled;
    }

    public boolean isMorningDeliveryEnabled() {
        return morningDeliveryEnabled;
    }

    public void setMorningDeliveryEnabled(boolean morningDeliveryEnabled) {
        this.morningDeliveryEnabled = morningDeliveryEnabled;
    }

    public boolean isDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(boolean dispatchEnabled) {
        this.dispatchEnabled = dispatchEnabled;
    }

    public boolean isBreakfastTipEnabled() {
        return breakfastTipEnabled;
    }

    public void setBreakfastTipEnabled(boolean breakfastTipEnabled) {
        this.breakfastTipEnabled = breakfastTipEnabled;
    }

    public boolean isCustomizationEnabled() {
        return customizationEnabled;
    }

    public void setCustomizationEnabled(boolean customizationEnabled) {
        this.customizationEnabled = customizationEnabled;
    }

    public boolean isRenewalReminderEnabled() {
        return renewalReminderEnabled;
    }

    public void setRenewalReminderEnabled(boolean renewalReminderEnabled) {
        this.renewalReminderEnabled = renewalReminderEnabled;
    }

    public boolean isMarketingEnabled() {
        return marketingEnabled;
    }

    public void setMarketingEnabled(boolean marketingEnabled) {
        this.marketingEnabled = marketingEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
