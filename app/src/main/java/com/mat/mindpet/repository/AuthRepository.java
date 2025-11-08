package com.mat.mindpet.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mat.mindpet.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

public class AuthRepository {

    private final FirebaseAuth auth;
    private final UserRepository userRepository;

    @Inject
    public AuthRepository(FirebaseAuth auth, UserRepository userRepository) {
        this.auth = auth;
        this.userRepository = userRepository;
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser firebaseUser);
        void onFailure(String errorMessage);
    }


}
