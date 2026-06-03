package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterDeviceRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Device token is required")
    @Size(min = 10, max = 500, message = "Device token must be between 10 and 500 characters")
    private String deviceToken;

    @NotBlank(message = "Platform is required")
    @Pattern(regexp = "^(android|ios)$", message = "Platform must be either 'android' or 'ios'")
    private String platform;

    @Size(max = 20, message = "App version must not exceed 20 characters")
    private String appVersion;

    public RegisterDeviceRequest() {
    }

    public RegisterDeviceRequest(Long userId, String deviceToken, String platform, String appVersion) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.platform = platform;
        this.appVersion = appVersion;
    }

    // Getters and Setters
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
}
