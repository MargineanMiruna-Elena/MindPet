package com.mat.mindpet.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mat.mindpet.R;
import com.mat.mindpet.model.Task;
import com.mat.mindpet.utils.NavigationHelper;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.util.TimeZone;
import java.time.LocalDate;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private Set<CalendarDay> goalDays = new HashSet<>();
    TextView textTasksCount;
    TextView textScreenGoalsCount;
    TextView textDailyScoreCount;
    TextView textStreakCount;
    private FirebaseFirestore db;
    private String currentUserId;

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

        Calendar cal = Calendar.getInstance();
        goalDays.add(CalendarDay.from(cal));

        cal.add(Calendar.DAY_OF_MONTH, -1);
        goalDays.add(CalendarDay.from(cal));

        cal.add(Calendar.DAY_OF_MONTH, -1);
        goalDays.add(CalendarDay.from(cal));

        int streak = calculateStreak();

        calendarView.addDecorator(new StreakDecorator(this, streak, R.color.indigo));

        NavigationHelper.setupNavigationBar(this);
    }

    private void updateLowerSection(CalendarDay selectedDay) {
        Calendar todayCal = Calendar.getInstance();
        Calendar selectedCal = selectedDay.getCalendar();

        if (selectedCal.after(todayCal)) {
            textTasksCount.setText("-");
            textScreenGoalsCount.setText("-");
            textDailyScoreCount.setText("-");
            textStreakCount.setText("-");
            return;
        }

        getCompletedTasksForDate(selectedDay, completedTasks -> {
            textTasksCount.setText(String.valueOf(completedTasks.size()));
            int dailyScore = calculateDailyScore(completedTasks);
            textDailyScoreCount.setText(String.valueOf(dailyScore));
        });

        getScreenGoalsForDate(selectedDay, screenGoalsMet -> {
            textScreenGoalsCount.setText(String.valueOf(screenGoalsMet));
        });

        int streak = calculateStreakUpTo(selectedDay);
        textStreakCount.setText(streak + " days");
    }

    private int calculateStreak() {
        int streak = 0;
        Calendar today = Calendar.getInstance();
        while (true) {
            CalendarDay day = CalendarDay.from(today);
            if (goalDays.contains(day)) {
                streak++;
                today.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                break;
            }
        }
        return streak;
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

    private int calculateDailyScore(List<Task> tasks) {
        int score = 0;
        for (Task task : tasks) {
            if (task.getPriority() != null) {
                switch (task.getPriority()) {
                    case "Urgent":
                        score += 40;
                        break;
                    case "Important":
                        score += 30;
                        break;
                    case "Normal":
                        score += 20;
                        break;
                    case "Nice-to-have":
                        score += 10;
                        break;
                }
            }
        }
        return score;
    }

    private void getScreenGoalsForDate(CalendarDay day, final ScreenGoalsCallback callback) {
        LocalDate selectedDate = LocalDate.of(day.getYear(), day.getMonth(), day.getDay());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = selectedDate.format(formatter);

        db.collection("screentime")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("date", formattedDate)
                .whereLessThanOrEqualTo("exceededGoalBy", 0)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onCallback(task.getResult().size());
                    } else {
                        callback.onCallback(0);
                    }
                });
    }

    interface ScreenGoalsCallback {
        void onCallback(int screenGoalsMet);
    }

    private int calculateStreakUpTo(CalendarDay day) {
        int streak = 0;
        Calendar cal = day.getCalendar();
        while (goalDays.contains(CalendarDay.from(cal))) {
            streak++;
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        return streak;
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
