package com.mat.mindpet.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mat.mindpet.model.User;
import com.mat.mindpet.repository.UserRepository;

import javax.inject.Inject;

public class AuthService {

    private final FirebaseAuth auth;
    private final UserRepository userRepository;

    @Inject
    public AuthService(FirebaseAuth auth, UserRepository userRepository) {
        this.auth = auth;
        this.userRepository = userRepository;
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser firebaseUser);
        void onFailure(String errorMessage);
    }

    public void signUp(String email, String password, User user, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            user.setUserId(firebaseUser.getUid());
                            userRepository.createUser(user);
                        }
                        callback.onSuccess(firebaseUser);
                    } else {
                        callback.onFailure(task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error");
                    }
                });
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(auth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error");
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}
