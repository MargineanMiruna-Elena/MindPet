package com.mat.mindpet.domain;

import java.time.LocalDate;

public class AppUsage {

    private String screentimeId;
    private String appName;
    private long minutesUsed;
    private long goalMinutes;

    public AppUsage(String screentimeId, String appName, long minutesUsed, long goalMinutes) {
        this.screentimeId = screentimeId;
        this.appName = appName;
        this.minutesUsed = minutesUsed;
        this.goalMinutes = goalMinutes;
    }

    public String getScreentimeId() {
        return screentimeId;
    }

    public void setScreentimeId(String screentimeId) {
        this.screentimeId = screentimeId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMinutesUsed() {
        return minutesUsed;
    }

    public void setMinutesUsed(long minutesUsed) {
        this.minutesUsed = minutesUsed;
    }

    public long getGoalMinutes() {
        return goalMinutes;
    }

    public void setGoalMinutes(long goalMinutes) {
        this.goalMinutes = goalMinutes;
    }
}
