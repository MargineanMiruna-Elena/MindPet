package com.mat.mindpet.utils;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mat.mindpet.R;
import com.mat.mindpet.activity.UsageStatsActivity;
import com.mat.mindpet.adapter.AppUsageAdapter;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.service.ScreentimeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class LimitDialogHelper {

    private Context context;
    private final ScreentimeService screentimeService;

    @Inject
    public LimitDialogHelper(@ApplicationContext Context context, ScreentimeService screentimeService) {
        this.context = context;
        this.screentimeService = screentimeService;
    }

    public void showAddLimitDialog(AppCompatActivity activity, List<AppUsage> appUsageList, AppUsageAdapter adapter) {

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_add_limit, null);

        Spinner spinnerOptions = dialogView.findViewById(R.id.spinnerApps);
        NumberPicker hoursPicker = dialogView.findViewById(R.id.npHours);
        NumberPicker minutesPicker = dialogView.findViewById(R.id.npMinutes);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        btnDelete.setVisibility(View.GONE);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(10);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        List<String> installedApps = getInstalledApps(activity);
        spinnerOptions.setAdapter(new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_dropdown_item,
                installedApps));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .create();

        btnAdd.setOnClickListener(v -> {
            Map<String, Integer> usageNow = UsageStatsHelper.getUsage(activity);
            String selectedApp = spinnerOptions.getSelectedItem().toString();
            long totalMinutes = hoursPicker.getValue() * 60 + minutesPicker.getValue();
            long usedMinutes = usageNow.getOrDefault(selectedApp, 0);

            if (totalMinutes == 0) {
                Toast.makeText(activity, "Please set a valid time limit", Toast.LENGTH_SHORT).show();
                return;
            }

            screentimeService.addLimit(
                    activity,
                    selectedApp,
                    usedMinutes,
                    totalMinutes,
                    () -> {
                        AppUsage a = new AppUsage(null, selectedApp, usedMinutes, totalMinutes);
                        appUsageList.add(a);
                        adapter.notifyItemInserted(appUsageList.size() - 1);
                        Toast.makeText(activity, "Limit added!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    },
                    () -> Toast.makeText(activity, "Limit already exists!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
            );
        });

        dialog.show();
    }

    private static List<String> getInstalledApps(AppCompatActivity activity) {
        List<String> appNames = new ArrayList<>();
        PackageManager pm = activity.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        for (ResolveInfo info : pm.queryIntentActivities(intent, 0)) {
            appNames.add(info.loadLabel(pm).toString());
        }

        Collections.sort(appNames);
        return appNames;
    }

    public void showEditLimitDialog(
            UsageStatsActivity activity,
            AppUsage appUsage,
            ScreentimeService screentimeService,
            AppUsageAdapter adapter
    ) {

        View dialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_edit_limit, null);

        NumberPicker hoursPicker = dialogView.findViewById(R.id.npHours);
        NumberPicker minutesPicker = dialogView.findViewById(R.id.npMinutes);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(10);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        long goal = appUsage.getGoalMinutes();
        hoursPicker.setValue((int) (goal / 60));
        minutesPicker.setValue((int) (goal % 60));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            int total = hoursPicker.getValue() * 60 + minutesPicker.getValue();
            screentimeService.updateLimit(appUsage.getAppName(), total,
                    () -> {
                        appUsage.setGoalMinutes(total);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    },
                    err -> Toast.makeText(activity, err, Toast.LENGTH_SHORT).show(),
                    activity
            );
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle("Delete limit?")
                    .setMessage("Are you sure you want to delete this limit?")
                    .setPositiveButton("Delete", (d, w) -> {
                        screentimeService.deleteLimit(
                                appUsage.getScreentimeId(),
                                () -> {
                                    adapter.removeItem(appUsage);
                                    dialog.dismiss();
                                },
                                err -> Toast.makeText(activity, err, Toast.LENGTH_SHORT).show(),
                                activity
                        );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        dialog.show();
    }


}
