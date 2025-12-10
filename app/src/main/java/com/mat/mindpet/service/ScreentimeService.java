package com.mat.mindpet.service;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.domain.StatsSummary;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.repository.ProgressRepository;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.mat.mindpet.utils.UsageStatsHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.inject.Inject;

public class ScreentimeService {

    private final AuthService authService;
    private final ScreentimeRepository repository;
    private final ProgressService progressService;;


    public interface StatsCallback {
        void onSuccess(StatsSummary summary);

        void onFailure(String error);
    }

    @Inject
    public ScreentimeService(AuthService authService, ScreentimeRepository repository, ProgressService progressService) {
        this.authService = authService;
        this.repository = repository;
        this.progressService = progressService;
    }

    public void addLimit(AppCompatActivity activity, String selectedApp, long usedToday,
                         long totalMinutes, Runnable onSuccess, Runnable onDuplicate,
                         Consumer<String> onError) {

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(activity, "You must be logged in to add limits", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        repository.checkIfLimitExists(userId, selectedApp, new ScreentimeRepository.ScreentimeCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                        usedToday,
                        totalMinutes
                );
                repository.saveScreentime(screentime);

                updateScreenGoalsMet(activity);

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

    public void updateLimit(String appName, int newGoal, Runnable onSuccess, Consumer<String> onError, Context activity) {

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
                        updateScreenGoalsMet(activity);
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

    public void deleteLimit(String screentimeId, Runnable onSuccess, Consumer<String> onError, Context context) {
        repository.deleteLimit(
                screentimeId,
                onSuccess,
                error -> onError.accept(error)
        );
        updateScreenGoalsMet(context);
    }

    public void getStatsSummary(Context ctx, StatsCallback callback) {
        try {
            long startOfToday = UsageStatsHelper.getStartOfDay(0);
            long endOfToday = UsageStatsHelper.getEndOfDay(0);

            long startYesterday = UsageStatsHelper.getStartOfDay(-1);
            long endYesterday = UsageStatsHelper.getEndOfDay(-1);

            long startWeek = UsageStatsHelper.getStartOfWeek();
            long endWeek = UsageStatsHelper.getEndOfWeek();

            StatsSummary summary = new StatsSummary();

            summary.setTodayScreenTime(UsageStatsHelper.getTotalScreenTime(ctx, startOfToday, endOfToday));
            summary.setYesterdayScreenTime(UsageStatsHelper.getTotalScreenTime(ctx, startYesterday, endYesterday));
            summary.setWeeklyScreenTime(UsageStatsHelper.getTotalScreenTime(ctx, startWeek, endWeek));

            summary.setTodayNotifications(UsageStatsHelper.getNotificationCount(ctx, startOfToday, endOfToday));
            summary.setYesterdayNotifications(UsageStatsHelper.getNotificationCount(ctx, startYesterday, endOfToday));
            summary.setWeeklyNotifications(UsageStatsHelper.getNotificationCount(ctx, startWeek, endWeek));

            callback.onSuccess(summary);

        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void updateAllLimitsInBackground(Context context) {
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        repository.getScreentimesByUser(userId, new ScreentimeRepository.ScreentimesCallback() {
            @Override
            public void onSuccess(List<Screentime> screentimes) {
                Map<String, Integer> usageNow = UsageStatsHelper.getUsage(context);
                int totalLimits = screentimes.size();
                int exceededLimits = 0;

                for (Screentime s : screentimes) {
                    int usedToday = usageNow.getOrDefault(s.getAppName(), 0);

                    if (usedToday > s.getGoalMinutes()) {
                        exceededLimits++;
                    }

                    updateUsedMinutes(s.getScreentimeId(), usedToday);
                }

                int percentMet = totalLimits == 0 ? 100 : ((totalLimits - exceededLimits) * 100) / totalLimits;

                updateScreenGoalsMetToday(userId, percentMet);
            }

            @Override
            public void onFailure(DatabaseError error) {
            }
        });
    }

    public void updateScreenGoalsMet(Context context) {
        repository.getScreentimesByUser(
                authService.getCurrentUser().getUid(),
                new ScreentimeRepository.ScreentimesCallback() {
                    @Override
                    public void onSuccess(List<Screentime> screentimes) {
                        int totalLimits = screentimes.size();
                        int exceededLimits = 0;
                        Map<String, Integer> usageNow = UsageStatsHelper.getUsage(context);

                        for (Screentime s : screentimes) {
                            int usedToday = usageNow.getOrDefault(s.getAppName(), 0);

                            if (usedToday > s.getGoalMinutes()) {
                                exceededLimits++;
                            }
                        }

                        int percentMet = totalLimits == 0 ? 100 : ((totalLimits - exceededLimits) * 100) / totalLimits;
                        updateScreenGoalsMetToday(
                                authService.getCurrentUser().getUid(),
                                percentMet
                        );
                    }

                    @Override
                    public void onFailure(DatabaseError error) {

                    }
                });
    }

    public void updateScreenGoalsMetToday(String userId, int percentMet) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();
        long endOfDay = startOfDay + 86400000 - 1;

        progressService.loadProgressForRange(userId, startOfDay, endOfDay, new ProgressService.ProgressCallback() {
            @Override
            public void onSuccess(Progress progress) {

                if (progress == null) {
                    final AtomicBoolean isSuccess = new AtomicBoolean(false);
                    progressService.saveProgress(
                            () -> {
                                isSuccess.set(true);
                            },
                            (errorMessage) -> {
                                isSuccess.set(false);
                            }
                    );

                } else {
                    progressService.updateProgressField(progress.getUserId(), "screenGoalsMet", percentMet);
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
            }
        });
    }

    public void updateNotificationSent(String screentimeId, boolean sent) {
        repository.updateNotificationSent(screentimeId, sent);
    }
}
