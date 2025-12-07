package com.mat.mindpet.model.enums;

public enum Mood {
    HAPPY, SAD, NEUTRAL;

    public String getDescription() {
        switch (this) {
            case HAPPY:
                return "Your pet is thriving and full of energy!";
            case SAD:
                return "Your pet is feeling down and unhappy.";
            case NEUTRAL:
                return "Your pet is feeling okay.";
            default:
                return "";
        }
    }

    public String getDisplayName() {
        switch (this) {
            case HAPPY:
                return "Happy";
            case SAD:
                return "Sad";
            case NEUTRAL:
                return "Neutral";
            default:
                return "";
        }
    }
}
