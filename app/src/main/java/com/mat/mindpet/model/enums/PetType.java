package com.mat.mindpet.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum PetType {
    BROWN_DOG, HUSKEY, MIX_DOG, ORANGE_CAT, GRAY_CAT, BROWN_CAT;

    private static final Map<String, PetType> PET_MAP = new HashMap<>();
    static {
        PET_MAP.put("Husky", PetType.HUSKEY);
        PET_MAP.put("Brown Dog", PetType.BROWN_DOG);
        PET_MAP.put("Mix Dog", PetType.MIX_DOG);
        PET_MAP.put("Orange Cat", PetType.ORANGE_CAT);
        PET_MAP.put("Grey Cat", PetType.GRAY_CAT);
        PET_MAP.put("Brown Cat", PetType.BROWN_CAT);
    }

    public static PetType getPetTypeFromString(String petName) {
        PetType type = PET_MAP.get(petName);
        if (type == null) throw new IllegalArgumentException("Unknown pet name: " + petName);
        return type;
    }
}
