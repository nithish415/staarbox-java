package com.example.demo.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Day-of-week rules for scheduled push notifications.
 * Saturday = delivery day (no customization reminders).
 * Sunday = no delivery (customization reminders only).
 */
public final class NotificationSchedulePolicy {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private NotificationSchedulePolicy() {
    }

    public static boolean shouldSendDeliveryNotifications() {
        return shouldSendDeliveryNotifications(LocalDate.now(IST));
    }

    public static boolean shouldSendCustomizationNotifications() {
        return shouldSendCustomizationNotifications(LocalDate.now(IST));
    }

    public static boolean shouldSendDeliveryNotifications(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    public static boolean shouldSendCustomizationNotifications(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY;
    }
}
