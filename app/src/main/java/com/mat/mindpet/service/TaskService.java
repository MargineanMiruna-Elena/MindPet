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

    private final TaskRepository taskRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    private String localDateToString(LocalDate date) {
        return date != null ? date.format(formatter) : null;
    }


    private LocalDate stringToLocalDate(String str) {
        try {
            return str != null ? LocalDate.parse(str, formatter) : LocalDate.now();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }


    public interface TasksCallback {
        void onSuccess(List<Task> tasks);
        void onFailure(String errorMessage);
    }


    public interface TaskCreatedCallback {
        void onSuccess(Task task);
        void onFailure(String errorMessage);
    }


    public void createTask(Task task, TaskCreatedCallback callback) {

        taskRepository.createTask(task);


        String createdAtStr = localDateToString(task.getCreatedAt());
        String deadlineStr = localDateToString(task.getDeadline());

        if (createdAtStr != null) {
            taskRepository.updateTaskField(task.getTaskId(), "createdAt", createdAtStr);
        }
        if (deadlineStr != null) {
            taskRepository.updateTaskField(task.getTaskId(), "deadline", deadlineStr);
        }


        callback.onSuccess(task);
    }


    public void getTasksByUser(String userId, TasksCallback callback) {
        taskRepository.getTasksByUser(userId, new TaskRepository.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasksFromRepo) {
                List<Task> tasks = new ArrayList<>();
                for (Task t : tasksFromRepo) {


                    taskRepository.updateTaskField(t.getTaskId(), "completed", null);

                    LocalDate createdAt = t.getCreatedAt() != null
                            ? stringToLocalDate(t.getCreatedAt().toString())
                            : LocalDate.now();
                    LocalDate deadline = t.getDeadline() != null
                            ? stringToLocalDate(t.getDeadline().toString())
                            : null;

                    Task task = new Task(
                            t.getTaskId(),
                            t.getUserId(),
                            t.getTitle(),
                            deadline,
                            t.isCompleted(),
                            t.getRewardPoints(),
                            createdAt
                    );
                    tasks.add(task);
                }
                callback.onSuccess(tasks);
            }

            @Override
            public void onFailure(DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }



    public void updateTaskField(String taskId, String fieldName, Object value) {
        if ("createdAt".equals(fieldName) && value instanceof LocalDate) {
            value = localDateToString((LocalDate) value);
        } else if ("deadline".equals(fieldName) && value instanceof LocalDate) {
            value = localDateToString((LocalDate) value);
        }
        taskRepository.updateTaskField(taskId, fieldName, value);
    }
}
