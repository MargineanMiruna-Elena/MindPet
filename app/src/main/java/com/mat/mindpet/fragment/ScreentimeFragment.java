package com.mat.mindpet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mat.mindpet.R;

public class ScreentimeFragment extends Fragment {

    private static final String ARG_INTERVAL = "interval";
    private String interval;

    private TextView tvOverallUsage;
    private TextView tvUnlocks;
    private TextView tvNotifications;

    public static ScreentimeFragment newInstance(String interval) {
        ScreentimeFragment fragment = new ScreentimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INTERVAL, interval);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screentime, container, false);

        tvOverallUsage = view.findViewById(R.id.tvOverallUsage);
        tvUnlocks = view.findViewById(R.id.tvUnlocks);
        tvNotifications = view.findViewById(R.id.tvNotifications);

        if (getArguments() != null) {
            interval = getArguments().getString(ARG_INTERVAL, "Daily");
        } else {
            interval = "Daily";
        }

        updateUsageData(interval);

        return view;
    }

    private void updateUsageData(String filter) {
        int intervalType;
        switch (filter) {
            case "Daily": intervalType = 0; break;
            case "Weekly": intervalType = 1; break;
            case "Yearly": intervalType = 2; break;
            default: intervalType = 0; break;
        }

        long usageHours = getScreenTime(intervalType);
        tvOverallUsage.setText("Screentime: " + (usageHours / 60) + "h " + (usageHours % 60) + "m");

        int unlocks = getUnlockCount(intervalType);
        tvUnlocks.setText("Unlocks: " + unlocks);

        int notifications = getNotificationCount(intervalType);
        tvNotifications.setText("Notifications: " + notifications);
    }

    private long getScreenTime(int intervalType) {
        switch (intervalType) {
            case 0: return 265;
            case 1: return 245;
            case 2: return 320;
            default: return 0;
        }
    }

    private int getUnlockCount(int intervalType) {
        switch (intervalType) {
            case 0: return 55;
            case 1: return 47;
            case 2: return 53;
            default: return 0;
        }
    }

    private int getNotificationCount(int intervalType) {
        switch (intervalType) {
            case 0: return 135;
            case 1: return 95;
            case 2: return 102;
            default: return 0;
        }
    }
}
