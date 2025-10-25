package com.mat.mindpet.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.mat.mindpet.R;
import com.mat.mindpet.activity.AccountActivity;
import com.mat.mindpet.activity.HomeActivity;
import com.mat.mindpet.activity.ToDoListActivity;
import com.mat.mindpet.activity.CalendarActivity;
import com.mat.mindpet.activity.UsageStatsActivity;

public class NavigationHelper {

    // Method that sets up the navigation bar on each page
    public static void setupNavigationBar(Activity activity) {
        View navHome = activity.findViewById(R.id.buttonHome);
        View navTodo = activity.findViewById(R.id.buttonTodo);
        View navCalendar = activity.findViewById(R.id.buttonCalendar);
        View navScreenTime = activity.findViewById(R.id.buttonScreenTime);
        View navAccount = activity.findViewById(R.id.buttonAccount);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                if (!(activity instanceof HomeActivity)) {
                    activity.startActivity(new Intent(activity, HomeActivity.class));
                }
            });
        }

        if (navTodo != null) {
            navTodo.setOnClickListener(v -> {
                if (!(activity instanceof ToDoListActivity)) {
                    activity.startActivity(new Intent(activity, ToDoListActivity.class));
                }
            });
        }

        if (navCalendar != null) {
            navCalendar.setOnClickListener(v -> {
                if (!(activity instanceof CalendarActivity)) {
                    activity.startActivity(new Intent(activity, CalendarActivity.class));
                }
            });
        }

        if (navScreenTime != null) {
            navScreenTime.setOnClickListener(v -> {
                if (!(activity instanceof UsageStatsActivity)) {
                    activity.startActivity(new Intent(activity, UsageStatsActivity.class));
                }
            });
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                if (!(activity instanceof AccountActivity)) {
                    activity.startActivity(new Intent(activity, AccountActivity.class));
                }
            });
        }
    }
}

