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
import java.util.concurrent.atomic.AtomicInteger;
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

        screentimeRepository.getScreentimesByUser(userId, new ScreentimeRepository.ScreentimesCallback() {
            @Override
            public void onSuccess(List<Screentime> userScreentimes) {
                progress.setScreenGoalsMet(calculateScreenGoalsMetPercentage(userScreentimes));
                if (progress.getScreenGoalsMet() == 100) {
                   progress.setStreak(true);
                } else {
                    progress.setStreak(false);
                }
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
                AtomicInteger completedCount = new AtomicInteger(0);
                AtomicInteger dailyScore = new AtomicInteger(0);

                long startOfDay = startOfDay(System.currentTimeMillis());
                long endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1;

                for (Task task : tasks) {
                    long deadline = task.getDeadline();
                    if (task.getIsCompleted() && deadline >= startOfDay && deadline <= endOfDay) {
                        completedCount.incrementAndGet();
                        dailyScore.addAndGet(getPointsForPriority(task.getPriority()));
                    }
                }

                progress.setDailyScore(dailyScore.get());
                progress.setTasksCompleted(completedCount.get());

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
        System.out.println(totalScreentimes);
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

    public void loadProgressForRange(String userId, long startMillis, long endMillis, ProgressCallback callback) {
        progressRepository.getProgressInRange(userId, startMillis, endMillis, new ProgressRepository.ProgressCallback() {
            @Override
            public void onSuccess(Progress progress) {
                callback.onSuccess(progress);
            }

            @Override
            public void onFailure(DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    public void updateProgressField(String progressId, String userId, String fieldName, Object value) {
        progressRepository.updateProgressField(progressId, userId, fieldName, value);
    }

    public void calculateStreakCount(String userId, long date, StreakCallback callback) {
        progressRepository.getAllProgressByUser(userId, new ProgressRepository.ProgressListCallback() {
            @Override
            public void onSuccess(List<Progress> progressList) {
                progressList.sort((a, b) -> Long.compare(b.getDate(), a.getDate()));

                int streakCount = 0;

                for (Progress p : progressList) {
                    if (p.getStreak() && startOfDay(p.getDate()) <= startOfDay(date)) {
                        streakCount++;
                    } else if (!p.getStreak()) {
                        break;
                    }
                }

                callback.onResult(streakCount);
            }

            @Override
            public void onFailure(DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    public interface StreakCallback {
        void onResult(int streakCount);
        void onError(DatabaseError error);
    }

    private long startOfDay(long dateMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}