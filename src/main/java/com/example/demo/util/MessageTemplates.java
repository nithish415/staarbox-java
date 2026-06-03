package com.example.demo.util;

/**
 * Message templates for push notifications. All templates are versioned for A/B
 * testing and analytics.
 */
public class MessageTemplates {

    // Notification Types (for preferences and logging)
    public static final String TYPE_FOOD_READY = "food_ready";
    public static final String TYPE_MORNING_DELIVERY = "morning_delivery";
    public static final String TYPE_DISPATCH = "dispatch";
    public static final String TYPE_BREAKFAST_TIP = "breakfast_tip";
    public static final String TYPE_CUSTOMIZATION_OPEN = "customization_open";
    public static final String TYPE_CUSTOMIZATION_REMINDER = "customization_reminder";
    public static final String TYPE_LAST_REMINDER = "last_reminder";
    public static final String TYPE_CUSTOMIZATION_CLOSED = "customization_closed";
    public static final String TYPE_RENEWAL_REMINDER = "renewal_reminder";
    public static final String TYPE_MARKETING = "marketing";

    // Titles
    public static final String TITLE_FOOD_READY = "Food Ready!";
    public static final String TITLE_MORNING_DELIVERY = "Morning Delivery";
    public static final String TITLE_CUSTOM_OPEN = "Customization Open";
    public static final String TITLE_REMINDER = "Reminder";
    public static final String TITLE_LAST_REMINDER = "Last Chance!";
    public static final String TITLE_CLOSED = "Customization Closed";
    public static final String TITLE_RENEWAL = "Resume Your Plan";
    public static final String TITLE_DISPATCH = "On the Way!";
    public static final String TITLE_BREAKFAST_TIP = "Breakfast Tip";

    // Message Bodies
    public static final String MSG_FOOD_READY = "Your healthy meal is getting ready. Wake up to freshness!";

    public static final String MSG_MORNING_DELIVERY = "Your box is on its way, fresh and made just for you.";

    public static final String MSG_CUSTOMIZATION_OPEN = "Your meal customization is now open. Make it yours!";

    public static final String MSG_CUSTOMIZATION_REMINDER = "Don't forget to customize your meal. Your preferences matter!";

    public static final String MSG_LAST_REMINDER = "Last call! Finish your customization before it closes.";

    public static final String MSG_CLOSED = "Customization closed. Your fresh meal is being prepared!";

    /**
     * Lapsed subscribers only ({@code NextRenewalDate} before today). Up to
     * {@link com.example.demo.service.NotificationService#RENEWAL_REMINDER_MAX_PER_WEEK}
     * per week with {@link com.example.demo.service.NotificationService#RENEWAL_REMINDER_MIN_GAP_DAYS}
     * between sends — see {@code NotificationService.sendLapsedSubscriptionRenewalReminders}.
     */
    public static final String MSG_RENEWAL = "Your plan is paused. Ready to resume your healthy routine?";

    public static final String MSG_DISPATCH = "Your meal is on the move! Track your delivery now.";

    public static final String MSG_BREAKFAST_TIP = "Start your day right. A good breakfast makes everything better!";

    /**
     * Get notification data for a specific type.
     */
    public static class NotificationData {
        public final String title;
        public final String message;
        public final String type;

        public NotificationData(String title, String message, String type) {
            this.title = title;
            this.message = message;
            this.type = type;
        }
    }

    public static NotificationData getFoodReady() {
        return new NotificationData(TITLE_FOOD_READY, MSG_FOOD_READY, TYPE_FOOD_READY);
    }

    public static NotificationData getMorningDelivery() {
        return new NotificationData(TITLE_MORNING_DELIVERY, MSG_MORNING_DELIVERY, TYPE_MORNING_DELIVERY);
    }

    public static NotificationData getDispatch() {
        return new NotificationData(TITLE_DISPATCH, MSG_DISPATCH, TYPE_DISPATCH);
    }

    public static NotificationData getBreakfastTip() {
        return new NotificationData(TITLE_BREAKFAST_TIP, MSG_BREAKFAST_TIP, TYPE_BREAKFAST_TIP);
    }

    public static NotificationData getCustomizationOpen() {
        return new NotificationData(TITLE_CUSTOM_OPEN, MSG_CUSTOMIZATION_OPEN, TYPE_CUSTOMIZATION_OPEN);
    }

    public static NotificationData getCustomizationReminder() {
        return new NotificationData(TITLE_REMINDER, MSG_CUSTOMIZATION_REMINDER, TYPE_CUSTOMIZATION_REMINDER);
    }

    public static NotificationData getLastReminder() {
        return new NotificationData(TITLE_LAST_REMINDER, MSG_LAST_REMINDER, TYPE_LAST_REMINDER);
    }

    public static NotificationData getClosed() {
        return new NotificationData(TITLE_CLOSED, MSG_CLOSED, TYPE_CUSTOMIZATION_CLOSED);
    }

    public static NotificationData getRenewal() {
        return new NotificationData(TITLE_RENEWAL, MSG_RENEWAL, TYPE_RENEWAL_REMINDER);
    }
}
