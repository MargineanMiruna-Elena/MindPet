package com.mat.mindpet.model;

import com.mat.mindpet.model.enums.Mood;
import com.mat.mindpet.model.enums.PetType;

public class Pet {
    private String petName;
    private PetType petType;
    private int level;
    private Mood mood;

    public Pet() {}

    public Pet(String petName, PetType petType, int level, Mood mood) {
        this.petName = petName;
        this.petType = petType;
        this.level = level;
        this.mood = mood;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public PetType getPetType() {
        return petType;
    }

    public void setPetType(PetType petType) {
        this.petType = petType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }
}
