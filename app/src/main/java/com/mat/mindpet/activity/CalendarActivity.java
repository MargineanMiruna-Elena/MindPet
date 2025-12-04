package com.mat.mindpet.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mat.mindpet.R;
import com.mat.mindpet.model.Progress;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.repository.ProgressRepository;
import com.mat.mindpet.utils.NavigationHelper;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.util.TimeZone;

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
    ProgressRepository progressRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        calendarView = findViewById(R.id.calendarView);
        textTasksCount = findViewById(R.id.textTasksCount);
        textScreenGoalsCount = findViewById(R.id.textScreenGoalsCount);
        textDailyScoreCount = findViewById(R.id.textDailyScoreCount);
        textStreakCount = findViewById(R.id.textStreakCount);

        CalendarDay today = CalendarDay.today();
        calendarView.setDateSelected(today, true);
        updateLowerSection(today);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            updateLowerSection(date);
        });

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

        progressRepository.getProgressInRange(currentUserId, startOfDay, endOfDay, new ProgressRepository.ProgressCallback() {
            @Override
            public void onSuccess(Progress progress) {
                if (progress != null) {
                    textTasksCount.setText(String.valueOf(progress.getTasksCompleted()));
                    textScreenGoalsCount.setText(progress.getScreenGoalsMet() + "%");
                    textDailyScoreCount.setText(String.valueOf(progress.getDailyScore()));
                    textStreakCount.setText(progress.getStreakCount() + " days");

                    for (int i = 0; i < progress.getStreakCount(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(progress.getDate());
                        calendar.add(Calendar.DAY_OF_MONTH, -i);
                        CalendarDay day = CalendarDay.from(calendar);
                        successDays.add(day);
                    }

                    calendarView.addDecorator(new StreakDecorator(CalendarActivity.this, successDays.size(), R.color.indigo));

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


    private void getCompletedTasksForDate(CalendarDay day, final TaskCallback callback) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(day.getYear(), day.getMonth() - 1, day.getDay(), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = cal.getTimeInMillis();

        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("isCompleted", true)
                .whereGreaterThanOrEqualTo("completedAt", startOfDay)
                .whereLessThan("completedAt", endOfDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task> completedTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            completedTasks.add(document.toObject(Task.class));
                        }
                        callback.onCallback(completedTasks);
                    } else {
                        callback.onCallback(new ArrayList<>());
                    }
                });
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
