package com.mat.mindpet.service;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.repository.ScreentimeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

public class ScreentimeService {

    private final AuthService authService;
    private final ScreentimeRepository repository;

    @Inject
    public ScreentimeService(AuthService authService, ScreentimeRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    public void addLimit(AppCompatActivity activity, List<AppUsage> appUsageList, String selectedApp,
                         int totalMinutes, Runnable onSuccess, Runnable onDuplicate,
                         java.util.function.Consumer<String> onError) {

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(activity, "You must be logged in to add limits", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        repository.checkIfLimitExists(userId, selectedApp, new ScreentimeRepository.ScreentimeCallback() {
            @Override
            public void onSuccess(Screentime existing) {
                if (existing != null) {
                    onDuplicate.run();
                    return;
                }

                Screentime screentime = new Screentime(
                        null,
                        userId,
                        selectedApp,
                        LocalDate.now(),
                        0,
                        totalMinutes,
                        0
                );
                repository.saveScreentime(screentime);
                onSuccess.run();
            }

            @Override
            public void onFailure(DatabaseError error) {
                onError.accept(error.getMessage());
            }
        });
    }
    public void getUserLimits(
            java.util.function.Consumer<List<Screentime>> onSuccess,
            java.util.function.Consumer<String> onError
    ) {

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            onError.accept("User not logged in");
            return;
        }

        String userId = currentUser.getUid();

        repository.getScreentimesByUser(userId, new ScreentimeRepository.ScreentimesCallback() {
            @Override
            public void onSuccess(List<Screentime> screentimes) {
                onSuccess.accept(screentimes);
            }

            @Override
            public void onFailure(DatabaseError error) {
                onError.accept(error.getMessage());
            }
        });
    }


    public void updateLimit(String appName, int newGoal, Runnable onSuccess, Consumer<String> onError) {

        FirebaseUser user = authService.getCurrentUser();
        if (user == null) {
            onError.accept("User not logged in");
            return;
        }

        repository.updateGoalMinutes(
                user.getUid(),
                appName,
                newGoal,
                new ScreentimeRepository.ScreentimeCallback() {
                    @Override
                    public void onSuccess(Screentime s) {
                        onSuccess.run();
                    }

                    @Override
                    public void onFailure(DatabaseError error) {
                        onError.accept(error.getMessage());
                    }
                }
        );
    }

}
