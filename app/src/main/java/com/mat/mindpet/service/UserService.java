package com.mat.mindpet.service;

import com.mat.mindpet.repository.UserRepository;

import javax.inject.Inject;

public class UserService {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Inject
    public UserService(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    public void getCurrentUser(UserRepository.UserCallback callback) {
        userRepository.getUserById(authService.getCurrentUser().getUid(), callback);
    }

    public void updateCurrentUserField(String fieldName, Object value) {
        userRepository.updateUserField(authService.getCurrentUser().getUid(), fieldName, value);
    }
}
