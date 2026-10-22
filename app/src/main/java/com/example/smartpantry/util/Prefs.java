package com.example.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper for managing application SharedPreferences.
 */
public class Prefs {
    private static final String PREF_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRING_SOON_ALERTS = "key_expiring_soon_alerts";
    private static final String KEY_UNITS_PREFERENCE = "key_units_preference";

    public static final String UNIT_PREF_METRIC = "Metric";
    public static final String UNIT_PREF_IMPERIAL = "Imperial";

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isExpiringSoonAlertsEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_EXPIRING_SOON_ALERTS, true);
    }

    public static void setExpiringSoonAlertsEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_EXPIRING_SOON_ALERTS, enabled).apply();
    }

    public static String getUnitsPreference(Context context) {
        return getPrefs(context).getString(KEY_UNITS_PREFERENCE, UNIT_PREF_METRIC);
    }

    public static void setUnitsPreference(Context context, String unitsPref) {
        getPrefs(context).edit().putString(KEY_UNITS_PREFERENCE, unitsPref).apply();
    }
}
