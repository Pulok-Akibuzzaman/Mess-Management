package com.project.messmanagement.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for currency formatting
 */
public class CurrencyUtils {

    /**
     * Format amount to currency (৳ symbol)
     */
    public static String formatCurrency(double amount) {
        return String.format("৳ %.2f", amount);
    }

    /**
     * Format amount to currency without decimals
     */
    public static String formatCurrencyNoDecimal(double amount) {
        return String.format("৳ %.0f", amount);
    }

    /**
     * Format amount as plain number with 2 decimals
     */
    public static String formatAmount(double amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(amount);
    }

    /**
     * Parse currency string to double
     */
    public static double parseCurrency(String currencyStr) {
        try {
            return Double.parseDouble(currencyStr.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Format large numbers with commas
     */
    public static String formatLargeNumber(long number) {
        return String.format("%,d", number);
    }

    /**
     * Format large decimal numbers with commas
     */
    public static String formatLargeDecimal(double number) {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(number);
    }

    /**
     * Get abbreviated currency (e.g., 1000 -> 1K)
     */
    public static String formatAbbreviatedCurrency(double amount) {
        if (amount >= 1_000_000) {
            return String.format("৳ %.1fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("৳ %.1fK", amount / 1_000);
        } else {
            return String.format("৳ %.2f", amount);
        }
    }

    /**
     * Safely convert to long
     */
    public static long safeLongConvert(double amount) {
        return Math.round(amount);
    }
}
