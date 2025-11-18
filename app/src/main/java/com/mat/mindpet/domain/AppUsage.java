package com.mat.mindpet.domain;

import java.time.LocalDate;

public class AppUsage {

    private String screentimeId;
    private String appName;
    private LocalDate date;
    private int minutesUsed;
    private int goalMinutes;
    private int exceededGoal;

    public AppUsage(String screentimeId, String appName, int minutesUsed, int goalMinutes) {
        this.screentimeId = screentimeId;
        this.appName = appName;
        this.minutesUsed = minutesUsed;
        this.goalMinutes = goalMinutes;
        this.exceededGoal = minutesUsed - goalMinutes;
        this.date = LocalDate.now();
    }

    public AppUsage(String screentimeId, String appName, LocalDate date, int minutesUsed, int goalMinutes) {
        this.screentimeId = screentimeId;
        this.appName = appName;
        this.date = date;
        this.minutesUsed = minutesUsed;
        this.goalMinutes = goalMinutes;
        this.exceededGoal = minutesUsed - goalMinutes;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getMinutesUsed() {
        return minutesUsed;
    }

    public void setMinutesUsed(int minutesUsed) {
        this.minutesUsed = minutesUsed;
    }

    public int getGoalMinutes() {
        return goalMinutes;
    }

    public void setGoalMinutes(int goalMinutes) {
        this.goalMinutes = goalMinutes;
    }

    public int getExceededGoal() {
        return exceededGoal;
    }

    public void setExceededGoal(int exceededGoal) {
        this.exceededGoal = exceededGoal;
    }
}
