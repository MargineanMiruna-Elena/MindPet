package com.mat.mindpet.utils;

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
}
