package com.example.smartpantry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for formatting and checking expiry dates.
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_FORMAT = "MMM dd, yyyy";

    public static String formatDate(Long epochMillis) {
        if (epochMillis == null || epochMillis == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());
        return sdf.format(new Date(epochMillis));
    }

    public static String formatDateIso(Long epochMillis) {
        if (epochMillis == null || epochMillis == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date(epochMillis));
    }

    public static Long parseDateIso(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = sdf.parse(dateStr.trim());
            return date != null ? date.getTime() : null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isExpired(Long epochMillis) {
        if (epochMillis == null || epochMillis == 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        // Compare against start of today
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return epochMillis < today.getTimeInMillis();
    }

    public static boolean isExpiringSoon(Long epochMillis, int daysWindow) {
        if (epochMillis == null || epochMillis == 0 || isExpired(epochMillis)) {
            return false;
        }
        long now = System.currentTimeMillis();
        long windowMillis = (long) daysWindow * 24 * 60 * 60 * 1000;
        return (epochMillis - now) <= windowMillis;
    }
}
