package com.mat.mindpet.model;

public class Task {

    private String taskId;
    private String userId;
    private String title;

    private long deadline;

    private boolean isCompleted;
    private String priority;

    private long createdAt;
    private long completedAt;

    public Task() {}

    public Task(String taskId, String userId, String title,
                long deadline, boolean isCompleted, String priority,
                long createdAt) {

        this.taskId = taskId;
        this.userId = userId;
        this.title = title;

        this.deadline = deadline;
        this.createdAt = createdAt;

        this.isCompleted = isCompleted;
        this.priority = priority;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(boolean completed) { isCompleted = completed; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public long getDeadline() { return deadline; }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public long getCreatedAt() { return createdAt; }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getCompletedAt() { return completedAt; }

    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
}
