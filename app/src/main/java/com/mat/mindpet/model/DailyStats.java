package com.mat.mindpet.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class DailyStats {

    private String userId;
    private Date date; // The specific day for these stats
    private int tasksCompletedCount;
    private int screenGoalsMetCount;
    private int dailyScore;

    @ServerTimestamp
    private Date lastUpdated;

    public DailyStats() {
        // Firestore requires a no-arg constructor
    }

    // --- Getters and Setters ---
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getTasksCompletedCount() { return tasksCompletedCount; }
    public void setTasksCompletedCount(int tasksCompletedCount) { this.tasksCompletedCount = tasksCompletedCount; }

    public int getScreenGoalsMetCount() { return screenGoalsMetCount; }
    public void setScreenGoalsMetCount(int screenGoalsMetCount) { this.screenGoalsMetCount = screenGoalsMetCount; }

    public int getDailyScore() { return dailyScore; }
    public void setDailyScore(int dailyScore) { this.dailyScore = dailyScore; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
