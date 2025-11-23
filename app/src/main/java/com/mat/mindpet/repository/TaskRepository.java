package com.mat.mindpet.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TaskRepository {

    private final DatabaseReference tasksRef;

    @Inject
    public TaskRepository(DatabaseReference databaseReference) {
        this.tasksRef = databaseReference.child("tasks");
    }

    public interface TaskCallback {
        void onSuccess(Task task);

        void onFailure(Exception e);
    }

    public interface TasksCallback {
        void onSuccess(List<Task> tasks);

        void onFailure(Exception e);
    }

    /**
     * Creates a new task
     */
    public void createTask(Task task) {
        String taskId = tasksRef.push().getKey();
        task.setTaskId(taskId);
        tasksRef.child(taskId).setValue(task);
    }

    /**
     * Updates a task
     */
    public void updateTask(Task task) {
        tasksRef.child(task.getTaskId()).setValue(task);
    }

    /**
     * Update a single field of a task
     */
    public void updateTaskField(String taskId, String fieldName, Object value) {
        tasksRef.child(taskId).child(fieldName).setValue(value);
    }

    /**
     * Deletes a task
     */
    public void deleteTask(String taskId) {
        tasksRef.child(taskId).removeValue();
    }

    /**
     * Retrieves a task with ID
     */
    public void getTaskById(String taskId, TaskCallback callback) {
        tasksRef.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    Task task = snapshot.getValue(Task.class);
                    callback.onSuccess(task);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(new Exception(error.getMessage()));
            }
        });
    }

    /**
     * Retrieves all task of a user
     */
    public void getTasksByUser(String userId, TasksCallback callback) {
        tasksRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            List<Task> tasks = new ArrayList<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Task task = child.getValue(Task.class);
                                tasks.add(task);
                            }
                            callback.onSuccess(tasks);
                        } catch (Exception e) {
                            callback.onFailure(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(new Exception(error.getMessage()));
                    }
                });
    }
}
