package org.pet.management.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.pet.management.config.AppConfig.getProp;

public class DateTimeUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(getProp("date.time.format"));

    public static String toString(final LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime from(final String dateString) {
        return LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
    }
}
