package com.mat.mindpet.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mat.mindpet.fragment.ScreentimeFragment;

public class UsagePagerAdapter extends FragmentStateAdapter {

    private final String[] tabs = {"Daily", "Weekly", "Yearly"};

    public UsagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return tabs.length;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ScreentimeFragment.newInstance(tabs[position]);
    }

    public String getTabTitle(int position) {
        return tabs[position];
    }
}
