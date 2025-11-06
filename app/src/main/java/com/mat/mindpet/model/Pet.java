package com.mat.mindpet.model;

public class Pet {
    private int petId;
    private int userId;
    private String petName;
    private int level;
    private String mood;

    public Pet(int petId, int userId, String petName, int level, String mood) {
        this.petId = petId;
        this.userId = userId;
        this.petName = petName;
        this.level = level;
        this.mood = mood;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
