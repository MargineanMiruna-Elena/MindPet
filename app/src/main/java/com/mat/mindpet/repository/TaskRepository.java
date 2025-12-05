package com.mat.mindpet.repository;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mat.mindpet.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TaskRepository {

    private final DatabaseReference tasksRef;
    private final DatabaseReference progressRef;

    @Inject
    public TaskRepository(DatabaseReference databaseReference) {
        this.tasksRef = databaseReference.child("tasks");
        this.progressRef = databaseReference.child("progress");
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
        tasksRef.child(taskId).setValue(task).addOnCompleteListener(e -> {
            updateProgressForUserAndDate(task.getUserId(), task.getDeadline());
        });
    }

    /**
     * Update a single field of a task
     */
    public void updateTaskField(String taskId, String fieldName, Object value) {
        getTaskById(taskId, new TaskCallback() {
            @Override
            public void onSuccess(Task oldTask) {

                long date = oldTask.getDeadline();
                String userId = oldTask.getUserId();

                tasksRef.child(taskId).child(fieldName).setValue(value)
                        .addOnCompleteListener(v -> {
                            if (fieldName.equals("isCompleted")) {
                                updateProgressForUserAndDate(userId, date);
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) { }
        });
    }

    /**
     * Deletes a task
     */
    public void deleteTask(String taskId) {
        getTaskById(taskId, new TaskCallback() {
            @Override
            public void onSuccess(Task task) {

                long date = task.getDeadline();
                String userId = task.getUserId();

                tasksRef.child(taskId).removeValue()
                        .addOnCompleteListener(t ->
                                updateProgressForUserAndDate(userId, date));
            }

            @Override
            public void onFailure(Exception e) { }
        });
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

    private void updateProgressForUserAndDate(String userId, long dateTimestamp) {

        if (userId == null) return;

        tasksRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot taskSnap) {
                        try {
                            long start = startOfDay(dateTimestamp);
                            long end = start + 24L * 60L * 60L * 1000L;
                            AtomicInteger completedCount = new AtomicInteger(0);
                            AtomicInteger dailyScore = new AtomicInteger(0);


                            for (DataSnapshot t : taskSnap.getChildren()) {
                                Task task = t.getValue(Task.class);

                                if (task != null &&
                                        task.getDeadline() >= start &&
                                        task.getDeadline() < end &&
                                        task.getIsCompleted()) {
                                    completedCount.incrementAndGet();
                                    dailyScore.addAndGet(scorePerTask(task.getPriority()));
                                }
                            }

                            progressRef.orderByChild("userId").equalTo(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot progressSnap) {
                                            long start = startOfDay(dateTimestamp);
                                            long end = start + 24L * 60L * 60L * 1000L;

                                            for (DataSnapshot p : progressSnap.getChildren()) {

                                                Long date = p.child("date").getValue(Long.class);
                                                if (date != null && date >= start && date < end) {

                                                    p.getRef().child("tasksCompleted")
                                                            .setValue(completedCount.get());

                                                    p.getRef().child("dailyScore")
                                                            .setValue(dailyScore.get());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) { }
                                    });
                        } catch (Exception e) {
                            Log.e("TaskRepository", "Error updating progress: " + e.getMessage());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("TaskRepository", "Error fetching tasks: " + error.getMessage());
                    }
                });
    }

    private long startOfDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private int scorePerTask(String priority) {
        if (priority == null) return 0;
        switch (priority) {
            case "Urgent":
                return 50;
            case "Important":
                return 30;
            case "Normal":
                return 20;
            case "Nice-to-have":
                return 10;
            default:
                return 0;
        }
    }
}
