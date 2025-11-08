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

    public void signUp(String email, String password, String firstName, String lastName, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            String joinDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                            User newUser = new User(userId, firstName, lastName, email, password, joinDate, null);

                            
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }
}
