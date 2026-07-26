package com.huarenzaimeng.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");
    public static final ZoneId DHAKA = ZoneId.of("Asia/Dhaka");

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtil() {
    }

    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(UTC);
    }

    public static String toBeijingDisplay(LocalDateTime utcTime) {
        if (utcTime == null) {
            return "";
        }
        ZonedDateTime utcZoned = utcTime.atZone(UTC);
        return utcZoned.withZoneSameInstant(BEIJING).format(DISPLAY_FORMATTER);
    }

    public static String toDhakaDisplay(LocalDateTime utcTime) {
        if (utcTime == null) {
            return "";
        }
        ZonedDateTime utcZoned = utcTime.atZone(UTC);
        return utcZoned.withZoneSameInstant(DHAKA).format(DISPLAY_FORMATTER);
    }

    public static LocalDateTime fromEpochSecond(long epochSecond) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), UTC);
    }
}
