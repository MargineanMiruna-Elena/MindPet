package com.mat.mindpet.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.ProgressRepository;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.mat.mindpet.repository.TaskRepository;
import com.mat.mindpet.utils.UsageStatsHelper;

import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

public class ProgressService {

    private final AuthService authService;
    private final ProgressRepository progressRepository;
    private final ScreentimeRepository screentimeRepository;
    private final TaskRepository taskRepository;

    @Inject
    public ProgressService(AuthService authService, ProgressRepository progressRepository, ScreentimeRepository screentimeRepository, TaskRepository taskRepository) {
        this.authService = authService;
        this.progressRepository = progressRepository;
        this.screentimeRepository = screentimeRepository;
        this.taskRepository = taskRepository;
    }

    public void saveProgress(Runnable onSuccess, Consumer<String> onError) {
        Progress progress = new Progress();
        FirebaseUser authUser = authService.getCurrentUser();

        if (authUser == null) {
            onError.accept("User not logged in");
            return;
        }

        String userId = authUser.getUid();
        progress.setUserId(userId);
        progress.setDate(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startOfYesterday = cal.getTimeInMillis();

        long endOfYesterday = startOfYesterday + (24 * 60 * 60 * 1000) - 1;

        progressRepository.getProgressInRange(userId, startOfYesterday, endOfYesterday, new ProgressRepository.ProgressCallback() {
            @Override
            public void onSuccess(Progress yesterdayProgress) {
                int currentStreak = 1;

                if (yesterdayProgress != null) {
                    currentStreak = yesterdayProgress.getStreakCount() + 1;
                }

                progress.setStreakCount(currentStreak);

                fetchScreentimeAndContinue(userId, progress, onSuccess, onError);
            }

            @Override
            public void onFailure(DatabaseError error) {
                onError.accept(error.getMessage());
            }
        });
    }

    private void fetchScreentimeAndContinue(String userId, Progress progress, Runnable onSuccess, Consumer<String> onError) {
        screentimeRepository.getScreentimesByUser(userId, new ScreentimeRepository.ScreentimesCallback() {
            @Override
            public void onSuccess(List<Screentime> userScreentimes) {
                progress.setScreenGoalsMet(calculateScreenGoalsMetPercentage(userScreentimes));
                fetchTasksAndSave(userId, progress, onSuccess, onError);
            }

            @Override
            public void onFailure(DatabaseError error) {
                onError.accept(error.getMessage());
            }
        });
    }

    private void fetchTasksAndSave(String userId, Progress progress, Runnable onSuccess, Consumer<String> onError) {
        taskRepository.getTasksByUser(userId, new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                int dailyScore = 0;
                int tasksCompleted = 0;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                long startOfDay = cal.getTimeInMillis();
                long endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1;


                for (Task task : tasks) {
                    long deadline = task.getDeadline();
                    if (task.getIsCompleted() && deadline >= startOfDay && deadline <= endOfDay) {
                        dailyScore += getPointsForPriority(task.getPriority());
                        tasksCompleted++;
                    }
                }

                progress.setDailyScore(dailyScore);
                progress.setTasksCompleted(tasksCompleted);

                progressRepository.saveProgress(progress);
                onSuccess.run();
            }

            @Override
            public void onFailure(Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }

    private int getPointsForPriority(String priority) {
        if (priority == null) return 0;
        switch (priority.toLowerCase()) {
            case "urgent": return 50;
            case "important": return 30;
            case "normal": return 20;
            case "nice-to-have": return 10;
            default: return 0;
        }
    }

    private int calculateScreenGoalsMetPercentage(List<Screentime> totalScreentimes) {
        if (totalScreentimes == null || totalScreentimes.isEmpty()) return 0;
        int metCount = 0;
        for (Screentime s : totalScreentimes) {
            if (s.getMinutesUsed() <= s.getGoalMinutes()) metCount++;
        }
        return (int) ((metCount / (double) totalScreentimes.size()) * 100);
    }

    public void getTodayProgress(ProgressCallback callback) {
        String userId = authService.getCurrentUser().getUid();
        long start = UsageStatsHelper.getStartOfDay(0);
        long end = UsageStatsHelper.getEndOfDay(0);

        progressRepository.getProgressInRange(userId, start, end, new ProgressRepository.ProgressCallback() {
            @Override
            public void onSuccess(Progress progress) {
                Log.d("ProgressService", "Progress found: " + progress);
                callback.onSuccess(progress);
            }

            @Override
            public void onFailure(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    public interface ProgressCallback {
        void onSuccess(Progress progress);
        void onFailure(DatabaseError error);
    }

}