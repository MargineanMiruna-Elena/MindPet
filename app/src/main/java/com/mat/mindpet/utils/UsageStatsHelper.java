package com.mat.mindpet.utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageStatsHelper {


    public static Map<String, Integer> getUsage(Context context) {

        Map<String, Integer> usageMap = new HashMap<>();

        UsageStatsManager usm =
                (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long end = System.currentTimeMillis();
        long start = end - 1000L * 60L * 60L * 24L;

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

    public static int getTotalScreenTime(Context ctx, long start, long end) {
        UsageStatsManager usm =
                (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, start, end
        );

        if (stats == null) return 0;

        long total = 0;

        for (UsageStats s : stats) {
            long visible = s.getTotalTimeVisible();
            if (visible == 0) visible = s.getTotalTimeInForeground();
            total += visible;
        }

        return (int) (total / 1000 / 60);
    }

    public static int getNotificationCount(Context ctx, long start, long end) {
        UsageStatsManager usm =
                (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);

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
}
