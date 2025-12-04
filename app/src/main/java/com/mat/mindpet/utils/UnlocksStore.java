package com.mat.mindpet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UnlocksStore {

    private static final String PREF_UNLOCKS = "pref_unlocks";

    public static void addUnlock(Context ctx) {
        long now = System.currentTimeMillis();
        List<Long> unlocks = getAllUnlocks(ctx);
        unlocks.add(now);
        saveUnlocks(ctx, unlocks);
    }

    private static List<Long> getAllUnlocks(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_UNLOCKS, Context.MODE_PRIVATE);
        String stored = prefs.getString("timestamps", "");
        List<Long> result = new ArrayList<>();
        if (!stored.isEmpty()) {
            for (String s : stored.split(",")) {
                try { result.add(Long.parseLong(s)); } catch (Exception ignored) {}
            }
        }
        return result;
    }

    private static void saveUnlocks(Context ctx, List<Long> unlocks) {
        StringBuilder sb = new StringBuilder();
        for (long ts : unlocks) {
            sb.append(ts).append(",");
        }
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_UNLOCKS, Context.MODE_PRIVATE);
        prefs.edit().putString("timestamps", sb.toString()).apply();
    }

    public static int getUnlocks(Context ctx, long start, long end) {
        int count = 0;
        for (long ts : getAllUnlocks(ctx)) {
            if (ts >= start && ts <= end) count++;
        }
        return count;
    }

}
