package com.mat.mindpet.model;

import java.time.LocalDate;

public class Progress {
    private int progressId;
    private int userId;
    private LocalDate date;
    private int screenGoalsMet;
    private int dailyScore;
    private int streakCount;
    private int tasksCompleted;

    public Progress(int progressId, int userId, LocalDate date, int screenGoalsMet, int dailyScore, int streakCount, int tasksCompleted) {
        this.progressId = progressId;
        this.userId = userId;
        this.date = date;
        this.screenGoalsMet = screenGoalsMet;
        this.dailyScore = dailyScore;
        this.streakCount = streakCount;
        this.tasksCompleted = tasksCompleted;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getScreenGoalsMet() {
        return screenGoalsMet;
    }

    public void setScreenGoalsMet(int screenGoalsMet) {
        this.screenGoalsMet = screenGoalsMet;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDailyScore() {
        return dailyScore;
    }

    public void setDailyScore(int dailyScore) {
        this.dailyScore = dailyScore;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }
}
