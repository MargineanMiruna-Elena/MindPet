package com.mat.mindpet.utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageStatsHelper {

    private static final String PREF_NOTIFS = "pref_notifs";

    public static Map<String, Integer> getUsage(Context context) {
        Map<String, Integer> usageMap = new HashMap<>();

        UsageStatsManager usm =
                (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long start = getStartOfDay(0);
        long end = System.currentTimeMillis();

        List<UsageStats> stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
        );

        if (stats == null) return usageMap;

        for (UsageStats stat : stats) {
            long millis = stat.getTotalTimeInForeground();
            if (millis > 0) {
                int minutes = (int) (millis / 1000 / 60);
                String label = getAppLabel(context, stat.getPackageName());
                usageMap.put(label, minutes);
            }
        }

        return usageMap;
    }

    public static int getTotalScreenTime(Context ctx, long start, long end) {
        UsageStatsManager usm = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null) return 0;

        UsageEvents events = usm.queryEvents(start, end);
        UsageEvents.Event event = new UsageEvents.Event();

        long total = 0;
        java.util.Map<String, Long> foregroundMap = new java.util.HashMap<>();

        while (events.hasNextEvent()) {
            events.getNextEvent(event);

            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                foregroundMap.put(event.getPackageName(), event.getTimeStamp());
            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                Long fgTime = foregroundMap.get(event.getPackageName());
                if (fgTime != null) {
                    total += Math.max(0, Math.min(event.getTimeStamp(), end) - Math.max(fgTime, start));
                    foregroundMap.remove(event.getPackageName());
                }
            }
        }

        return (int) (total / 1000 / 60);
    }

    public static int getNotificationCount(Context ctx, long start, long end) {
        UsageStatsManager usm = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null) return 0;
        UsageEvents events = usm.queryEvents(start, end);
        UsageEvents.Event event = new UsageEvents.Event();
        int count = 0;
        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            if (event.getEventType() == 12) {
                count++;
            }
        }
        return count;
    }

    public static long getStartOfDay(int offsetDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, offsetDays);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getEndOfDay(int offsetDays) {
        return getStartOfDay(offsetDays) + 86400000 - 1;
    }

    public static long getStartOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getEndOfWeek() {
        return getStartOfWeek() + (7 * 86400000) - 1;
    }

    public static String getAppLabel(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName, 0)
            ).toString();
        } catch (Exception e) {
            return packageName;
        }
    }
}
