package com.mat.mindpet.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mat.mindpet.R;
import com.mat.mindpet.adapter.AppUsageAdapter;
import com.mat.mindpet.adapter.UsagePagerAdapter;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.domain.StatsSummary;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.ScreentimeService;
import com.mat.mindpet.utils.LimitDialogHelper;
import com.mat.mindpet.utils.NavigationHelper;
import com.mat.mindpet.utils.UsageStatsHelper;
import com.mat.mindpet.utils.UsageUpdateWorker;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsageStatsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private UsagePagerAdapter pagerUsageAdapter;
    private RecyclerView rvAppUsageList;
    private ImageButton btnAddLimit;
    private AppUsageAdapter adapter;
    private List<AppUsage> appUsageList;
    @Inject
    AuthService authService;
    @Inject
    ScreentimeService screentimeService;
    @Inject
    LimitDialogHelper limitDialogHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        rvAppUsageList = findViewById(R.id.rvLimitsList);
        btnAddLimit = findViewById(R.id.btnAddLimit);

        pagerUsageAdapter = new UsagePagerAdapter(this);
        viewPager.setAdapter(pagerUsageAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerUsageAdapter.getTabTitle(position))
        ).attach();
        viewPager.setCurrentItem(1, false);

        screentimeService.getStatsSummary(this, new ScreentimeService.StatsCallback() {
            @Override
            public void onSuccess(StatsSummary summary) {
                pagerUsageAdapter.setStatsSummary(summary);
                pagerUsageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
            }
        });

        appUsageList = new ArrayList<>();
        adapter = new AppUsageAdapter(appUsageList);
        rvAppUsageList.setLayoutManager(new LinearLayoutManager(this));
        rvAppUsageList.setAdapter(adapter);

        adapter.setOnEditClickListener(appUsage ->
                limitDialogHelper.showEditLimitDialog(
                        this, appUsage, screentimeService, adapter
                )
        );

        loadLimits();

        btnAddLimit.setOnClickListener(v ->
                limitDialogHelper.showAddLimitDialog(this, appUsageList, adapter)
        );

        NavigationHelper.setupNavigationBar(this);
    }

    private void loadLimits() {

        screentimeService.getUserLimits(
                screentimes -> {
                    int exceeded = 0;
                    int total = screentimes.size();
                    Map<String, Integer> usageNow = UsageStatsHelper.getUsage(this);

                    appUsageList.clear();

                    for (Screentime s : screentimes) {

                        int usedToday = usageNow.getOrDefault(s.getAppName(), 0);

                        screentimeService.updateUsedMinutes(
                                s.getScreentimeId(),
                                usedToday
                        );

                        if (usedToday > s.getGoalMinutes()) {
                            exceeded++;
                        }

                        AppUsage appUsage = new AppUsage(
                                s.getScreentimeId(),
                                s.getAppName(),
                                usedToday,
                                s.getGoalMinutes()
                        );

                        appUsageList.add(appUsage);
                    }

                    int percentMet = total == 0 ? 0 : ((total - exceeded) * 100) / total;
                    screentimeService.updateScreenGoalsMetToday(
                            authService.getCurrentUser().getUid(),
                            percentMet
                    );

                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error loading limits: " + error, Toast.LENGTH_SHORT).show()
        );
    }
}