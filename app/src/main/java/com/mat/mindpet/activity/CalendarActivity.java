package com.mat.mindpet.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mat.mindpet.R;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.TaskRepository;
import com.mat.mindpet.service.ProgressService;
import com.mat.mindpet.service.TaskService;
import com.mat.mindpet.utils.NavigationHelper;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private Set<CalendarDay> successDays = new HashSet<>();
    TextView textTasksCount;
    TextView textScreenGoalsCount;
    TextView textDailyScoreCount;
    TextView textStreakCount;
    private FirebaseFirestore db;
    private String currentUserId;

    @Inject
    ProgressService progressService;

    @Inject
    TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getCompletedTasksForDate(CalendarDay.today());

        calendarView = findViewById(R.id.calendarView);
        textTasksCount = findViewById(R.id.textTasksCount);
        textScreenGoalsCount = findViewById(R.id.textScreenGoalsCount);
        textDailyScoreCount = findViewById(R.id.textDailyScoreCount);
        textStreakCount = findViewById(R.id.textStreakCount);



        NavigationHelper.setupNavigationBar(this);
    }

    private void updateLowerSection(CalendarDay selectedDay) {
        textTasksCount.setText("-");
        textScreenGoalsCount.setText("-");
        textDailyScoreCount.setText("-");
        textStreakCount.setText("-");

        Calendar cal = selectedDay.getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();
        long endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1;

        progressService.loadProgressForRange(currentUserId, startOfDay, endOfDay, new ProgressService.ProgressCallback() {
            @Override
            public void onSuccess(Progress progress) {
                if (progress != null) {
                    AtomicInteger streakCt = new AtomicInteger();
                    progressService.calculateStreakCount(currentUserId, progress.getDate(), new ProgressService.StreakCallback() {
                        @Override
                        public void onResult(int streakCount) {
                            streakCt.set(streakCount);
                            textTasksCount.setText(String.valueOf(progress.getTasksCompleted()));
                            textScreenGoalsCount.setText(progress.getScreenGoalsMet() + "%");
                            textDailyScoreCount.setText(String.valueOf(progress.getDailyScore()));
                            textStreakCount.setText(streakCt.get() + " days");

                            for (int i = 0; i < streakCt.get(); i++) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(progress.getDate());
                                calendar.add(Calendar.DAY_OF_MONTH, -i);
                                CalendarDay day = CalendarDay.from(calendar);
                                successDays.add(day);
                            }

                            calendarView.addDecorator(new StreakDecorator(CalendarActivity.this, successDays.size(), R.color.indigo));

                        }

                        @Override
                        public void onError(DatabaseError error) {
                        }
                    });

                } else {
                    textTasksCount.setText("0");
                    textScreenGoalsCount.setText("0%");
                    textDailyScoreCount.setText("0");
                    textStreakCount.setText("0 days");
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(CalendarActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCompletedTasksForDate(CalendarDay day) {
        Calendar cal = day.getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();
        long endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1;

        taskService.getTasksByUser(new TaskRepository.TasksCallback() {

            @Override
            public void onSuccess(List<Task> tasks) {
                if (tasks != null) {
                    int completedCount = 0;
                    int calculatedScore = 0;

                    for (Task task : tasks) {
                        if (task.getIsCompleted() && task.getDeadline() >= startOfDay && task.getDeadline() < endOfDay) {
                            completedCount++;
                            calculatedScore += getPointsForPriority(task.getPriority());
                        }
                    }

                    final int finalCompletedCount = completedCount;
                    final int finalScore = calculatedScore;

                    progressService.loadProgressForRange(currentUserId, startOfDay, endOfDay, new ProgressService.ProgressCallback() {
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
                                progressService.updateProgressField(progress.getUserId(), "tasksCompleted", finalCompletedCount);
                                progressService.updateProgressField(progress.getUserId(), "dailyScore", finalScore);
                            }

                            CalendarDay today = CalendarDay.today();
                            calendarView.setDateSelected(today, true);
                            updateLowerSection(today);

                            calendarView.setOnDateChangedListener((widget, date, selected) -> {
                                updateLowerSection(date);
                            });
                        }

                        @Override
                        public void onFailure(DatabaseError error) {
                            Toast.makeText(CalendarActivity.this, "Error updating progress: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CalendarActivity.this, "Error fetching tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }

        private int getPointsForPriority (String priority){
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

        interface TaskCallback {
            void onCallback(List<Task> completedTasks);
        }

        private static class StreakDecorator implements DayViewDecorator {
            private final int streak;
            private final int color;

            StreakDecorator(AppCompatActivity activity, int streak, int colorResId) {
                this.streak = streak;
                this.color = ContextCompat.getColor(activity, colorResId);
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                Calendar today = Calendar.getInstance();
                for (int i = 0; i < streak; i++) {
                    if (CalendarDay.from(today).equals(day)) {
                        return true;
                    }
                    today.add(Calendar.DAY_OF_MONTH, -1);
                }
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(10, color));
            }
        }
    }
