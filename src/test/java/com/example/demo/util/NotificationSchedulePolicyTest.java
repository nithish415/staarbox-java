package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NotificationSchedulePolicyTest {

    @ParameterizedTest
    @MethodSource("deliveryNotificationCases")
    void shouldSendDeliveryNotifications(LocalDate date, boolean expected) {
        assertEquals(expected, NotificationSchedulePolicy.shouldSendDeliveryNotifications(date));
    }

    @ParameterizedTest
    @MethodSource("customizationNotificationCases")
    void shouldSendCustomizationNotifications(LocalDate date, boolean expected) {
        assertEquals(expected, NotificationSchedulePolicy.shouldSendCustomizationNotifications(date));
    }

    static Stream<Arguments> deliveryNotificationCases() {
        return Stream.of(
                Arguments.of(saturday(), true),
                Arguments.of(sunday(), false),
                Arguments.of(weekday(DayOfWeek.MONDAY), true),
                Arguments.of(weekday(DayOfWeek.FRIDAY), true));
    }

    static Stream<Arguments> customizationNotificationCases() {
        return Stream.of(
                Arguments.of(saturday(), false),
                Arguments.of(sunday(), true),
                Arguments.of(weekday(DayOfWeek.WEDNESDAY), true),
                Arguments.of(weekday(DayOfWeek.FRIDAY), true));
    }

    private static LocalDate saturday() {
        return LocalDate.of(2026, 6, 13);
    }

    private static LocalDate sunday() {
        return LocalDate.of(2026, 6, 14);
    }

    private static LocalDate weekday(DayOfWeek dayOfWeek) {
        LocalDate date = LocalDate.of(2026, 6, 15);
        while (date.getDayOfWeek() != dayOfWeek) {
            date = date.plusDays(1);
        }
        return date;
    }
}
