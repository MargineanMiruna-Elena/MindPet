package com.mat.mindpet.service;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.domain.StatsSummary;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.mat.mindpet.utils.DateHelper;
import com.mat.mindpet.utils.UnlockStore;
import com.mat.mindpet.utils.UsageStatsHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

public class ScreentimeService {

    private final AuthService authService;
    private final ScreentimeRepository repository;

    public interface StatsCallback {
        void onSuccess(StatsSummary summary);
        void onFailure(String error);
    }

    @Inject
    public ScreentimeService(AuthService authService, ScreentimeRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    public void addLimit(AppCompatActivity activity, List<AppUsage> appUsageList, String selectedApp,
                         int totalMinutes, Runnable onSuccess, Runnable onDuplicate,
                         Consumer<String> onError) {

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
            Consumer<List<Screentime>> onSuccess,
            Consumer<String> onError
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

    public void updateUsedMinutes(String screentimeId, int usedMinutes) {
        repository.updateMinutesUsed(screentimeId, usedMinutes);
    }

    public void deleteLimit(String screentimeId, Runnable onSuccess, Consumer<String> onError) {
        repository.deleteLimit(
                screentimeId,
                onSuccess,
                error -> onError.accept(error)
        );
    }

    public void getStatsSummary(Context ctx, StatsCallback callback) {
        try {
            long now = System.currentTimeMillis();

            long startToday = DateHelper.getStartOfDayMillis();
            long startYesterday = DateHelper.getStartOfYesterday();
            long startWeek = DateHelper.getStartOfLast7Days();

            StatsSummary summary = new StatsSummary();

            summary.setTodayScreenTime(
                    UsageStatsHelper.getTotalScreenTime(ctx, startToday, now)
            );

            summary.setYesterdayScreenTime(
                    UsageStatsHelper.getTotalScreenTime(ctx, startYesterday, startToday)
            );

            summary.setWeeklyScreenTime(
                    UsageStatsHelper.getTotalScreenTime(ctx, startWeek, now)
            );

            summary.setTodayUnlocks(
                    UnlockStore.getToday(ctx)
            );

            summary.setYesterdayUnlocks(0);

            summary.setWeeklyUnlocks(summary.getTodayUnlocks());

            summary.setTodayNotifications(
                    UsageStatsHelper.getNotificationCount(ctx, startToday, now)
            );

            summary.setYesterdayNotifications(
                    UsageStatsHelper.getNotificationCount(ctx, startYesterday, startToday)
            );

            summary.setWeeklyNotifications(
                    UsageStatsHelper.getNotificationCount(ctx, startWeek, now)
            );

            callback.onSuccess(summary);

        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }
}
