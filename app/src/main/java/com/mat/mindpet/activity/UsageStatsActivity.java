package com.mat.mindpet.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mat.mindpet.adapter.AppUsageAdapter;
import com.mat.mindpet.R;
import com.mat.mindpet.adapter.UsagePagerAdapter;
import com.mat.mindpet.domain.AppUsage;
import com.mat.mindpet.utils.NavigationHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsageStatsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        UsagePagerAdapter pagerUsageAdapter = new UsagePagerAdapter(this);
        viewPager.setAdapter(pagerUsageAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerUsageAdapter.getTabTitle(position))
        ).attach();


        RecyclerView rvAppUsageList = findViewById(R.id.rvLimitsList);
        rvAppUsageList.setLayoutManager(new LinearLayoutManager(this));

        List<AppUsage> appUsageList = new ArrayList<>();
        appUsageList.add(new AppUsage(1, 1, "Instagram", LocalDate.now(), 60, 30));
        appUsageList.add(new AppUsage(2, 1, "YouTube", LocalDate.now(), 12, 20));

        AppUsageAdapter adapter = new AppUsageAdapter(appUsageList);
        rvAppUsageList.setAdapter(adapter);

        NavigationHelper.setupNavigationBar(this);
    }
}
