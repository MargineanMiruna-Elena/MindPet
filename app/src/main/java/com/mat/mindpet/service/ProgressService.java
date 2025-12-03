package com.mat.mindpet.service;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.ProgressRepository;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.mat.mindpet.repository.TaskRepository;

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

        progress.setUserId(authUser.getUid());
        progress.setDate(System.currentTimeMillis());

        screentimeRepository.getScreentimesByUser(authUser.getUid(), new ScreentimeRepository.ScreentimesCallback() {
            @Override
            public void onSuccess(List<Screentime> userScreentimes) {
                progress.setScreenGoalsMet(userScreentimes.size());

                fetchTasksAndSave(authUser.getUid(), progress, onSuccess, onError);
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

                for (Task task : tasks) {
                    if (task.getIsCompleted()) {
                        dailyScore += getPointsForPriority(task.getPriority());
                    }
                }

                progress.setDailyScore(dailyScore);

                // TODO: You still need to implement calculateStreakCount logic
                // progress.setStreakCount(calculateStreakCount());

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
            case "urgent":
                return 50;
            case "important":
                return 30;
            case "normal":
                return 20;
            case "nice-to-have":
                return 10;
            default:
                return 0;
        }
    }
}
