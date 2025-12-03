package com.mat.mindpet.service;

import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.ProgressRepository;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.mat.mindpet.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class ProgressService {

    private final TaskRepository taskRepository;
    private final ScreentimeRepository screentimeRepository;
    private final ProgressRepository progressRepository;
    private final AuthService authService;

    @Inject
    public ProgressService(TaskRepository taskRepository, ScreentimeRepository screentimeRepository, ProgressRepository progressRepository, AuthService authService) {
        this.taskRepository = taskRepository;
        this.screentimeRepository = screentimeRepository;
        this.progressRepository = progressRepository;
        this.authService = authService;
    }

//    public void recalculateAndSaveProgressForToday() {
//        String userId = authService.getCurrentUser().getUid();
//        if (userId == null) return;
//
//        LocalDate today = LocalDate.now();
//
//        taskRepository.getCompletedTasksForDate(userId, today, completedTasks -> {
//            int tasksCompleted = completedTasks.size();
//            int dailyScore = calculateDailyScore(completedTasks);
//
//            screentimeRepository.getMetScreenGoalsForDate(userId, today, screenGoalsMet -> {
//
//                progressRepository.getProgressForDate(userId, today, progress -> {
//                    if (progress == null) {
//                        progress = new Progress();
//                        progress.setUserId(userId);
//                        progress.setDate(today);
//                    }
//
//                    progress.setTasksCompleted(tasksCompleted);
//                    progress.setDailyScore(dailyScore);
//                    progress.setScreenGoalsMet(screenGoalsMet);
//
//
//
//                    progressRepository.saveOrUpdateProgress(progress);
//                });
//            });
//        });
//    }

    private int calculateDailyScore(List<Task> tasks) {
        int score = 0;
        for (Task task : tasks) {
            if (task.getPriority() != null) {
                switch (task.getPriority()) {
                    case "Urgent": score += 40; break;
                    case "Important": score += 30; break;
                    case "Normal": score += 20; break;
                    case "Nice-to-have": score += 10; break;
                }
            }
        }
        return score;
    }
}
