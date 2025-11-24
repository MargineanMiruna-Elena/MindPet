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
import com.mat.mindpet.domain.StatsSummary;

public class ScreentimeFragment extends Fragment {

    private static final String ARG_INTERVAL = "interval";
    private static final String ARG_STATS = "stats";

    private String interval;
    private StatsSummary stats;

    private TextView tvOverallUsage;
    private TextView tvUnlocks;
    private TextView tvNotifications;

    public static ScreentimeFragment newInstance(String interval, StatsSummary stats) {
        ScreentimeFragment fragment = new ScreentimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INTERVAL, interval);
        args.putSerializable(ARG_STATS, stats);
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
            interval = getArguments().getString(ARG_INTERVAL);
            stats = (StatsSummary) getArguments().getSerializable(ARG_STATS);
        }

        updateUI();

        return view;
    }

    private void updateUI() {
        if (stats == null) return;

        switch (interval) {
            case "Yesterday":
                tvOverallUsage.setText(formatTime(stats.getYesterdayScreenTime()));
                tvUnlocks.setText(String.valueOf(stats.getYesterdayUnlocks()));
                tvNotifications.setText(String.valueOf(stats.getYesterdayNotifications()));
                break;

            case "Today":
                tvOverallUsage.setText(formatTime(stats.getTodayScreenTime()));
                tvUnlocks.setText(String.valueOf(stats.getTodayUnlocks()));
                tvNotifications.setText(String.valueOf(stats.getTodayNotifications()));
                break;

            case "This Week":
                tvOverallUsage.setText(formatTime(stats.getWeeklyScreenTime()));
                tvUnlocks.setText(String.valueOf(stats.getWeeklyUnlocks()));
                tvNotifications.setText(String.valueOf(stats.getWeeklyNotifications()));
                break;
        }
    }

    private String formatTime(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return  h + "h " + m + "m";
    }
}
