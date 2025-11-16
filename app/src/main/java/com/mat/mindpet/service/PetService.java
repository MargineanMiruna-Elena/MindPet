package com.mat.mindpet.service;

import com.mat.mindpet.model.Pet;
import com.mat.mindpet.repository.UserRepository;

import javax.inject.Inject;

public class PetService {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Inject
    public PetService(AuthService authService,UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    public void adoptPet(Pet pet) {
        userRepository.createOrUpdatePetForUser(authService.getCurrentUser().getUid(), pet);
    }

    public void getPetForCurrentUser(UserRepository.PetCallback callback) {
        userRepository.getPetByUserId(authService.getCurrentUser().getUid(), callback);
    }
}
