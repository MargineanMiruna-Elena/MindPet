package com.mat.mindpet.model;

import java.time.LocalDate;

public class Task {
    private String taskId;
    private String userId;
    private String title;
    private LocalDate deadline;
    private boolean isCompleted;
    private int rewardPoints;
    private LocalDate createdAt;

    public Task(String taskId, String userId, String title, LocalDate deadline, boolean isCompleted, int rewardPoints, LocalDate createdAt) {
        this.taskId = taskId;
        this.userId = userId;
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
        this.rewardPoints = rewardPoints;
        this.createdAt = createdAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
