package com.mat.mindpet.domain;

public class StatsSummary {
    int userId;
    private int dailyScreenTime;
    private int weeklyAverageScreenTime;
    private int yearlyAverageScreenTime;

    private int dailyUnlocks;
    private int weeklyAverageUnlocks;
    private int yearlyAverageUnlocks;

    private int dailyNotifications;
    private int weeklyAverageNotifications;
    private int yearlyAverageNotifications;

    public int getDailyScreenTime() {
        return dailyScreenTime;
    }

    public void setDailyScreenTime(int dailyScreenTime) {
        this.dailyScreenTime = dailyScreenTime;
    }

    public int getWeeklyAverageScreenTime() {
        return weeklyAverageScreenTime;
    }

    public void setWeeklyAverageScreenTime(int weeklyAverageScreenTime) {
        this.weeklyAverageScreenTime = weeklyAverageScreenTime;
    }

    public int getYearlyAverageScreenTime() {
        return yearlyAverageScreenTime;
    }

    public void setYearlyAverageScreenTime(int yearlyAverageScreenTime) {
        this.yearlyAverageScreenTime = yearlyAverageScreenTime;
    }

    public int getDailyUnlocks() {
        return dailyUnlocks;
    }

    public void setDailyUnlocks(int dailyUnlocks) {
        this.dailyUnlocks = dailyUnlocks;
    }

    public int getWeeklyAverageUnlocks() {
        return weeklyAverageUnlocks;
    }

    public void setWeeklyAverageUnlocks(int weeklyAverageUnlocks) {
        this.weeklyAverageUnlocks = weeklyAverageUnlocks;
    }

    public int getYearlyAverageUnlocks() {
        return yearlyAverageUnlocks;
    }

    public void setYearlyAverageUnlocks(int yearlyAverageUnlocks) {
        this.yearlyAverageUnlocks = yearlyAverageUnlocks;
    }

    public int getDailyNotifications() {
        return dailyNotifications;
    }

    public void setDailyNotifications(int dailyNotifications) {
        this.dailyNotifications = dailyNotifications;
    }

    public int getWeeklyAverageNotifications() {
        return weeklyAverageNotifications;
    }

    public void setWeeklyAverageNotifications(int weeklyAverageNotifications) {
        this.weeklyAverageNotifications = weeklyAverageNotifications;
    }

    public int getYearlyAverageNotifications() {
        return yearlyAverageNotifications;
    }

    public void setYearlyAverageNotifications(int yearlyAverageNotifications) {
        this.yearlyAverageNotifications = yearlyAverageNotifications;
    }
}
