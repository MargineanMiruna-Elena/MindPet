package com.mat.mindpet.domain;

import java.io.Serializable;

public class StatsSummary implements Serializable {

    private int yesterdayScreenTime;
    private int todayScreenTime;
    private int weeklyScreenTime;

    private int yesterdayUnlocks;
    private int todayUnlocks;
    private int weeklyUnlocks;

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

    public int getYesterdayUnlocks() {
        return yesterdayUnlocks;
    }

    public void setYesterdayUnlocks(int yesterdayUnlocks) {
        this.yesterdayUnlocks = yesterdayUnlocks;
    }

    public int getTodayUnlocks() {
        return todayUnlocks;
    }

    public void setTodayUnlocks(int todayUnlocks) {
        this.todayUnlocks = todayUnlocks;
    }

    public int getWeeklyUnlocks() {
        return weeklyUnlocks;
    }

    public void setWeeklyUnlocks(int weeklyUnlocks) {
        this.weeklyUnlocks = weeklyUnlocks;
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
