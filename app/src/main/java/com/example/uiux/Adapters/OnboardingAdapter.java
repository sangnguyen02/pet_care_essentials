package com.example.uiux.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uiux.Fragments.SigninTabFragment;
import com.example.uiux.Fragments.SignupTabFragment;

public class OnboardingAdapter extends FragmentStateAdapter {
    private Context context;
    int totalTabs;
    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity, int totalTabs) {
        super(fragmentActivity);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SigninTabFragment();
            case 1:
                return new SignupTabFragment();
            default:
                return new SigninTabFragment(); // Default fragment
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
