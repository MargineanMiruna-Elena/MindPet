package com.mat.mindpet.utils;

import android.app.AlertDialog;
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
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.dialog_add_limit, null);

        Spinner spinnerOptions = dialogView.findViewById(R.id.spinnerApps);
        NumberPicker hoursPicker = dialogView.findViewById(R.id.npHours);
        NumberPicker minutesPicker = dialogView.findViewById(R.id.npMinutes);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(10);
        hoursPicker.setValue(1);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setValue(0);

        List<String> installedApps = getInstalledApps(activity);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_dropdown_item,
                installedApps
        );
        spinnerOptions.setAdapter(spinnerAdapter);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .create();

        btnAdd.setOnClickListener(v -> {
            String selectedApp = spinnerOptions.getSelectedItem().toString();
            int hours = hoursPicker.getValue();
            int minutes = minutesPicker.getValue();
            int totalMinutes = hours * 60 + minutes;

            if (totalMinutes == 0) {
                Toast.makeText(activity, "Please set a valid time limit", Toast.LENGTH_SHORT).show();
                return;
            }

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
                        dialog.dismiss();
                    },
                    () -> Toast.makeText(activity, "A limit already exists for this app!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(activity, "Error saving limit: " + error, Toast.LENGTH_SHORT).show()
            );
        });

        dialog.show();
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
