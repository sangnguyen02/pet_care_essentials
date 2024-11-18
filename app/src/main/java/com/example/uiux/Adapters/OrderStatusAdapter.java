package com.example.uiux.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uiux.Fragments.User.Order.OrderFragment;
import com.example.uiux.Utils.OrderStatus;

public class OrderStatusAdapter extends FragmentStateAdapter {
    public OrderStatusAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(OrderFragment.TITLE, OrderStatus.getStatusName(position));
        args.putInt("status", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
