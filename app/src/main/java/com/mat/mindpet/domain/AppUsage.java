package com.mat.mindpet.domain;

import java.time.LocalDate;

public class AppUsage {
    int entryId;
    int userId;
    String appName;
    LocalDate date;
    int minutesUsed;
    int goalMinutes;
    int exceededGoal;

    public AppUsage(int entryId, int userId, String appName, LocalDate date, int minutesUsed, int goalMinutes) {
        this.entryId = entryId;
        this.userId = userId;
        this.appName = appName;
        this.date = date;
        this.minutesUsed = minutesUsed;
        this.goalMinutes = goalMinutes;
        this.exceededGoal = minutesUsed - goalMinutes;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public int getExceededGoal() {
        return exceededGoal;
    }

    public void setExceededGoal(int exceededGoal) {
        this.exceededGoal = exceededGoal;
    }
}
