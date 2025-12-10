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

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long start = getStartOfDay(0);
        long end = System.currentTimeMillis();

        UsageEvents events = usm.queryEvents(start, end);

        Map<String, Long> totalUsage = new HashMap<>();
        Map<String, Long> activeStarts = new HashMap<>();

        UsageEvents.Event event = new UsageEvents.Event();

        while (events.hasNextEvent()) {
            events.getNextEvent(event);

            String pkg = event.getPackageName();
            long timestamp = event.getTimeStamp();
            int type = event.getEventType();

            boolean isResume =
                    type == UsageEvents.Event.ACTIVITY_RESUMED ||
                            type == UsageEvents.Event.MOVE_TO_FOREGROUND;

            boolean isPause =
                    type == UsageEvents.Event.ACTIVITY_PAUSED ||
                            type == UsageEvents.Event.MOVE_TO_BACKGROUND ||
                            type == UsageEvents.Event.ACTIVITY_STOPPED;

            if (isResume) {
                activeStarts.put(pkg, timestamp);

            } else if (isPause) {
                Long startTime = activeStarts.remove(pkg);
                if (startTime != null) {
                    long duration = timestamp - startTime;
                    totalUsage.put(pkg,
                            totalUsage.getOrDefault(pkg, 0L) + duration);
                }
            }
        }

        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : activeStarts.entrySet()) {
            long duration = now - entry.getValue();
            totalUsage.put(entry.getKey(),
                    totalUsage.getOrDefault(entry.getKey(), 0L) + duration);
        }

        Map<String, Integer> finalMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : totalUsage.entrySet()) {
            int minutes = (int) (entry.getValue() / 1000 / 60);
            if (minutes > 0) {
                finalMap.put(getAppLabel(context, entry.getKey()), minutes);
            }
        }

        return finalMap;
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
