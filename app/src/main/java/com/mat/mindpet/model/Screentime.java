package com.mat.mindpet.model;

public class Screentime {
    private String screentimeId;
    private String userId;
    private String appName;
    private long minutesUsed;
    private long goalMinutes;

    private boolean notificationSent;

    public Screentime(String screentimeId, String userId, String appName, long minutesUsed, long goalMinutes) {
        this.screentimeId = screentimeId;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
}
