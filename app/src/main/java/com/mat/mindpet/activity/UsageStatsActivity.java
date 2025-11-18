package com.mat.mindpet.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.mat.mindpet.R;
import com.mat.mindpet.adapter.AppUsageAdapter;
import com.mat.mindpet.adapter.UsagePagerAdapter;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.model.Screentime;
import com.mat.mindpet.repository.ScreentimeRepository;
import com.mat.mindpet.repository.UserRepository;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.ScreentimeService;
import com.mat.mindpet.utils.LimitDialogHelper;
import com.mat.mindpet.utils.NavigationHelper;
import com.mat.mindpet.utils.UsageStatsHelper;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsageStatsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private RecyclerView rvAppUsageList;
    private Button btnAddLimit;
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

        UsagePagerAdapter pagerUsageAdapter = new UsagePagerAdapter(this);
        viewPager.setAdapter(pagerUsageAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerUsageAdapter.getTabTitle(position))
        ).attach();


        appUsageList = new ArrayList<>();
        adapter = new AppUsageAdapter(appUsageList);
        rvAppUsageList.setLayoutManager(new LinearLayoutManager(this));
        rvAppUsageList.setAdapter(adapter);

        adapter.setOnEditClickListener(appUsage -> {
            limitDialogHelper.showEditLimitDialog(
                    this,
                    appUsage,
                    screentimeService,
                    adapter
            );
        });

        loadLimits();

        btnAddLimit.setOnClickListener(v -> {
            limitDialogHelper.showAddLimitDialog(this, appUsageList, adapter);
        });


        NavigationHelper.setupNavigationBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLimits();
    }
    private void loadLimits() {

        screentimeService.getUserLimits(
                screentimes -> {

                    Map<String, Integer> usageNow = UsageStatsHelper.getUsage(this);

                    appUsageList.clear();

                    for (Screentime s : screentimes) {

                        int usedToday = usageNow.getOrDefault(s.getAppName(), 0);

                        screentimeService.updateUsedMinutes(
                                s.getScreentimeId(),
                                usedToday
                        );

                        AppUsage appUsage = new AppUsage(
                                s.getScreentimeId(),
                                s.getAppName(),
                                usedToday,
                                s.getGoalMinutes()
                        );

                        appUsageList.add(appUsage);
                    }

                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error loading limits: " + error, Toast.LENGTH_SHORT).show()
        );
    }






}