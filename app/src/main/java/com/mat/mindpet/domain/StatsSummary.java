package com.mat.mindpet.domain;

import java.io.Serializable;

public class StatsSummary implements Serializable {

    private int yesterdayScreenTime;
    private int todayScreenTime;
    private int weeklyScreenTime;

    private int yesterdayNotifications;
    private int todayNotifications;
    private int weeklyNotifications;

    public int getYesterdayScreenTime() {
        return yesterdayScreenTime;
    }

    public void setYesterdayScreenTime(int yesterdayScreenTime) {
        this.yesterdayScreenTime = yesterdayScreenTime;
    }

    public int getTodayScreenTime() {
        return todayScreenTime;
    }

    public void setTodayScreenTime(int todayScreenTime) {
        this.todayScreenTime = todayScreenTime;
    }

    public int getWeeklyScreenTime() {
        return weeklyScreenTime;
    }

    public void setWeeklyScreenTime(int weeklyScreenTime) {
        this.weeklyScreenTime = weeklyScreenTime;
    }

    public int getYesterdayNotifications() {
        return yesterdayNotifications;
    }

    public void setYesterdayNotifications(int yesterdayNotifications) {
        this.yesterdayNotifications = yesterdayNotifications;
    }

    public int getTodayNotifications() {
        return todayNotifications;
    }

    public void setTodayNotifications(int todayNotifications) {
        this.todayNotifications = todayNotifications;
    }

    public int getWeeklyNotifications() {
        return weeklyNotifications;
    }

    public void setWeeklyNotifications(int weeklyNotifications) {
        this.weeklyNotifications = weeklyNotifications;
    }
}
