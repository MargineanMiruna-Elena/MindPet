package com.mat.mindpet.model;

import java.time.LocalDate;

public class Screentime {
    private String screentimeId;
    private String userId;
    private String appName;
    private LocalDate date;
    private int minutesUsed;
    private int goalMinutes;
    private int exceededGoalBy;


    public Screentime(String screentimeId, String userId, String appName, LocalDate date, int minutesUsed, int goalMinutes, int exceededGoalBy) {
        this.screentimeId = screentimeId;
        this.userId = userId;
        this.appName = appName;
        this.date = date;
        this.minutesUsed = minutesUsed;
        this.goalMinutes = goalMinutes;
        this.exceededGoalBy = exceededGoalBy;
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

    public int getExceededGoalBy() {
        return exceededGoalBy;
    }

    public void setExceededGoalBy(int exceededGoalBy) {
        this.exceededGoalBy = exceededGoalBy;
    }
}
