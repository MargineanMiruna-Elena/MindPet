package com.mat.mindpet.service;

import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.TaskRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TaskService {

    private final AuthService authService;
    private final TaskRepository taskRepository;

    @Inject
    public TaskService(TaskRepository taskRepository, AuthService authService) {
        this.taskRepository = taskRepository;
        this.authService = authService;
    }

    public void createTask(Task task) {
        taskRepository.createTask(task);
    }

    public void getTasksByUser(TaskRepository.TasksCallback callback) {
        taskRepository.getTasksByUser(authService.getCurrentUser().getUid(), callback);
    }

    public void updateTaskField(String taskId, String fieldName, Object value) {
        taskRepository.updateTaskField(taskId, fieldName, value);
    }
}
