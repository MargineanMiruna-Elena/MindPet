package com.mat.mindpet.utils;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.adapter.AppUsageAdapter;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.service.ScreentimeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class LimitDialogHelper {

    private final ScreentimeService screentimeService;

    @Inject
    public LimitDialogHelper(ScreentimeService screentimeService) {
        this.screentimeService = screentimeService;
    }

    public void showAddLimitDialog(AppCompatActivity activity, List<AppUsage> appUsageList, AppUsageAdapter adapter) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Limit");

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        Spinner appSpinner = new Spinner(activity);
        List<String> installedApps = getInstalledApps(activity);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_dropdown_item,
                installedApps
        );
        appSpinner.setAdapter(spinnerAdapter);
        layout.addView(appSpinner);

        TextView timeLabel = new TextView(activity);
        timeLabel.setText("Set time limit:");
        timeLabel.setPadding(0, 30, 0, 10);
        layout.addView(timeLabel);

        LinearLayout timeLayout = new LinearLayout(activity);
        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeLayout.setGravity(Gravity.CENTER);

        NumberPicker hoursPicker = new NumberPicker(activity);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(10);
        hoursPicker.setValue(1);
        timeLayout.addView(hoursPicker);

        TextView hoursLabel = new TextView(activity);
        hoursLabel.setText(" h ");
        hoursLabel.setTextSize(18f);
        hoursLabel.setPadding(10, 0, 10, 0);
        timeLayout.addView(hoursLabel);

        NumberPicker minutesPicker = new NumberPicker(activity);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setValue(0);
        timeLayout.addView(minutesPicker);

        TextView minutesLabel = new TextView(activity);
        minutesLabel.setText(" min");
        minutesLabel.setTextSize(18f);
        minutesLabel.setPadding(10, 0, 0, 0);
        timeLayout.addView(minutesLabel);

        layout.addView(timeLayout);
        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String selectedApp = appSpinner.getSelectedItem().toString();
            int hours = hoursPicker.getValue();
            int minutes = minutesPicker.getValue();
            int totalMinutes = hours * 60 + minutes;

            screentimeService.addLimit(
                    activity,
                    appUsageList,
                    selectedApp,
                    totalMinutes,
                    () -> {
                        AppUsage appUsage = new AppUsage(
                                appUsageList.size() + 1,
                                1,
                                selectedApp,
                                LocalDate.now(),
                                0,
                                totalMinutes
                        );
                        appUsageList.add(appUsage);
                        adapter.notifyItemInserted(appUsageList.size() - 1);
                        Toast.makeText(activity, "Limit successfully added!", Toast.LENGTH_SHORT).show();
                    },
                    () -> Toast.makeText(activity, "A limit already exists for this app!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(activity, "Error saving limit: " + error, Toast.LENGTH_SHORT).show()
            );
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private static List<String> getInstalledApps(AppCompatActivity activity) {
        List<String> appNames = new ArrayList<>();
        PackageManager pm = activity.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : resolveInfos) {
            String appName = info.loadLabel(pm).toString();
            appNames.add(appName);
        }
        Collections.sort(appNames);
        return appNames;
    }
}
