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

    public static void setupNavigationBar(Activity activity) {
        View navHome = activity.findViewById(R.id.buttonHome);
        View navTodo = activity.findViewById(R.id.buttonTodo);
        View navCalendar = activity.findViewById(R.id.buttonCalendar);
        View navScreenTime = activity.findViewById(R.id.buttonScreenTime);
        View navAccount = activity.findViewById(R.id.buttonAccount);

        View[] buttons = {navHome, navTodo, navCalendar, navScreenTime, navAccount};

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                if (!(activity instanceof HomeActivity)) {
                    activity.startActivity(new Intent(activity, HomeActivity.class));
                }
                setSelectedButton(buttons, navHome);
            });
        }

        if (navTodo != null) {
            navTodo.setOnClickListener(v -> {
                if (!(activity instanceof ToDoListActivity)) {
                    activity.startActivity(new Intent(activity, ToDoListActivity.class));
                }
                setSelectedButton(buttons, navTodo);
            });
        }

        if (navCalendar != null) {
            navCalendar.setOnClickListener(v -> {
                if (!(activity instanceof CalendarActivity)) {
                    activity.startActivity(new Intent(activity, CalendarActivity.class));
                }
                setSelectedButton(buttons, navCalendar);
            });
        }

        if (navScreenTime != null) {
            navScreenTime.setOnClickListener(v -> {
                if (!(activity instanceof UsageStatsActivity)) {
                    activity.startActivity(new Intent(activity, UsageStatsActivity.class));
                }
                setSelectedButton(buttons, navScreenTime);
            });
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                if (!(activity instanceof AccountActivity)) {
                    activity.startActivity(new Intent(activity, AccountActivity.class));
                }
                setSelectedButton(buttons, navAccount);
            });
        }


        if (activity instanceof HomeActivity) setSelectedButton(buttons, navHome);
        else if (activity instanceof ToDoListActivity) setSelectedButton(buttons, navTodo);
        else if (activity instanceof CalendarActivity) setSelectedButton(buttons, navCalendar);
        else if (activity instanceof UsageStatsActivity) setSelectedButton(buttons, navScreenTime);
        else if (activity instanceof AccountActivity) setSelectedButton(buttons, navAccount);
    }
    private static void setSelectedButton(View[] buttons, View selectedButton) {
        for (View button : buttons) {
            if (button != null) {
                button.setSelected(button == selectedButton);
            }
        }
    }
}
