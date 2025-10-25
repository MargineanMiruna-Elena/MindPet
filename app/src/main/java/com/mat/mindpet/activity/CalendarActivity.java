package com.mat.mindpet.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.mat.mindpet.R;
import com.mat.mindpet.utils.NavigationHelper;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private Set<CalendarDay> goalDays = new HashSet<>();
    TextView textTasks;
    TextView textScreenGoals;
    TextView textDailyScore;
    TextView textStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);

        textTasks = findViewById(R.id.textTasks);
        textScreenGoals = findViewById(R.id.textScreenGoals);
        textDailyScore = findViewById(R.id.textDailyScore);
        textStreak = findViewById(R.id.textStreak);

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
            textTasks.setText("");
            textScreenGoals.setText("");
            textDailyScore.setText("");
            textStreak.setText("");
            return;
        }

        int tasksCompleted = getTasksForDate(selectedDay);
        int screenGoalsMet = getScreenGoalsForDate(selectedDay);
        int dailyScore = getDailyScoreForDate(selectedDay);
        int streak = calculateStreakUpTo(selectedDay);

        textTasks.setText("Tasks completed: " + tasksCompleted);
        textScreenGoals.setText("Screen goals met: " + screenGoalsMet);
        textDailyScore.setText("Daily score: " + dailyScore);
        textStreak.setText("Current streak: " + streak + " days");
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

    private int getTasksForDate(CalendarDay day) {
        // TODO: fetch real tasks for the day
        return 3; // example
    }

    private int getScreenGoalsForDate(CalendarDay day) {
        return 2; // example
    }

    private int getDailyScoreForDate(CalendarDay day) {
        return 85; // example
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
