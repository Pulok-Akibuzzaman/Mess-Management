package com.project.messmanagement.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date formatting and operations
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String MONTH_FORMAT = "yyyy-MM";
    private static final String DISPLAY_FORMAT = "dd MMM yyyy";
    private static final String DISPLAY_TIME_FORMAT = "dd MMM yyyy hh:mm a";

    /**
     * Get current date as string (YYYY-MM-DD)
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Get current date-time as string (YYYY-MM-DD HH:mm:ss)
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    /**
     * Get current month as string (YYYY-MM)
     */
    public static String getCurrentMonth() {
        return YearMonth.now().format(DateTimeFormatter.ofPattern(MONTH_FORMAT));
    }

    /**
     * Format date for display (dd MMM yyyy)
     */
    public static String formatDateForDisplay(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return date.format(DateTimeFormatter.ofPattern(DISPLAY_FORMAT, Locale.getDefault()));
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Format date-time for display (dd MMM yyyy hh:mm a)
     */
    public static String formatDateTimeForDisplay(String dateTimeStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr,
                DateTimeFormatter.ofPattern(DATETIME_FORMAT));
            return dateTime.format(DateTimeFormatter.ofPattern(DISPLAY_TIME_FORMAT, Locale.getDefault()));
        } catch (Exception e) {
            return dateTimeStr;
        }
    }

    /**
     * Get month name from date string
     */
    public static String getMonthName(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get day name from date string
     */
    public static String getDayName(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Convert date string to another format
     */
    public static String convertDateFormat(String dateStr, String fromFormat, String toFormat) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(fromFormat));
            return date.format(DateTimeFormatter.ofPattern(toFormat));
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Get days difference between two dates
     */
    public static long getDaysDifference(String date1, String date2) {
        try {
            LocalDate d1 = LocalDate.parse(date1, DateTimeFormatter.ofPattern(DATE_FORMAT));
            LocalDate d2 = LocalDate.parse(date2, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return date.equals(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get previous month
     */
    public static String getPreviousMonth() {
        return YearMonth.now().minusMonths(1).format(DateTimeFormatter.ofPattern(MONTH_FORMAT));
    }

    /**
     * Get next month
     */
    public static String getNextMonth() {
        return YearMonth.now().plusMonths(1).format(DateTimeFormatter.ofPattern(MONTH_FORMAT));
    }

    /**
     * Parse string date
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Get first day of month
     */
    public static String getFirstDayOfMonth() {
        return LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Get last day of month
     */
    public static String getLastDayOfMonth() {
        return LocalDate.now().withDayOfMonth(
            LocalDate.now().lengthOfMonth()).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
}
