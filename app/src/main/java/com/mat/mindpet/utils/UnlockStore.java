package com.mat.mindpet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UnlockStore {

    private static final String PREF = "unlock_stats";
    private static final String KEY_TODAY = "today_unlocks";
    private static final String KEY_LAST_RESET = "last_reset";

    public static void increment(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        long lastReset = sp.getLong(KEY_LAST_RESET, 0);
        long todayStart = DateHelper.getStartOfDayMillis();

        if (lastReset < todayStart) {

            sp.edit()
                    .putInt(KEY_TODAY, 0)
                    .putLong(KEY_LAST_RESET, System.currentTimeMillis())
                    .apply();
        }

        int count = sp.getInt(KEY_TODAY, 0);
        sp.edit()
                .putInt(KEY_TODAY, count + 1)
                .apply();
    }

    public static int getToday(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getInt(KEY_TODAY, 0);
    }
}
