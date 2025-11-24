package com.mat.mindpet.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mat.mindpet.domain.StatsSummary;
import com.mat.mindpet.fragment.ScreentimeFragment;

public class UsagePagerAdapter extends FragmentStateAdapter {

    private final String[] tabs = {"Yesterday", "Today", "This Week"};

    private StatsSummary statsSummary;

    public UsagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setStatsSummary(StatsSummary summary) {
        this.statsSummary = summary;
    }

    @Override
    public int getItemCount() {
        return tabs.length;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ScreentimeFragment.newInstance(tabs[position], statsSummary);
    }

    public String getTabTitle(int position) {
        return tabs[position];
    }
}
