package com.mat.mindpet.model;

public class Progress {
    private String userId;
    private long date; //ziua pentru care se inregistreaza progresul
    private int screenGoalsMet; //procentul limitelor nedepasite din numarul de limite setate
    private int dailyScore; //scorul zilnic calculat pe baza rewardurilor de la taskurile indeplinite
    private boolean streak; //true daca utilizatorul a avut screenGoalsMet 100%, false altfel
    private int tasksCompleted; //numarul de taskuri completate in acea zi

    public Progress(){}

    public Progress(String userId, long date, int screenGoalsMet, int dailyScore, boolean streak, int tasksCompleted) {
        this.userId = userId;
        this.date = date;
        this.screenGoalsMet = screenGoalsMet;
        this.dailyScore = dailyScore;
        this.streak = streak;
        this.tasksCompleted = tasksCompleted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getScreenGoalsMet() {
        return screenGoalsMet;
    }

    public void setScreenGoalsMet(int screenGoalsMet) {
        this.screenGoalsMet = screenGoalsMet;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDailyScore() {
        return dailyScore;
    }

    public void setDailyScore(int dailyScore) {
        this.dailyScore = dailyScore;
    }

    public boolean getStreak() {
        return streak;
    }

    public void setStreak(boolean streak) {
        this.streak = streak;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }
}
