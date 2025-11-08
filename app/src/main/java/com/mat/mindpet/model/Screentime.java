package com.mat.mindpet.model;

import java.time.LocalDate;

public class Screentime {
    private int screentimeId;
    private int userId;
    private String appName;
    private LocalDate date;
    private int minutesUsed;
    private int goalMinutes;
    private int exceededGoalBy;

    public Screentime(int screentimeId, int userId, String appName, LocalDate date, int minutesUsed, int goalMinutes, int exceededGoalBy) {
        this.screentimeId = screentimeId;
        this.userId = userId;
        this.appName = appName;
        this.date = date;
        this.minutesUsed = minutesUsed;
        this.goalMinutes = goalMinutes;
        this.exceededGoalBy = exceededGoalBy;
    }

    public int getScreentimeId() {
        return screentimeId;
    }

    public void setScreentimeId(int screentimeId) {
        this.screentimeId = screentimeId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
