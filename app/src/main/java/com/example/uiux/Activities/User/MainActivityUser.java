package com.example.uiux.Activities.User;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.uiux.Fragments.User.CaringFragment;
import com.example.uiux.Fragments.User.CartFragment;
import com.example.uiux.Fragments.User.HomeFragment;
import com.example.uiux.Fragments.User.ProfileFragment;
import com.example.uiux.Fragments.User.WishlistFragment;
import com.example.uiux.R;
import com.example.uiux.databinding.ActivityMainUserBinding;
import com.google.android.material.badge.BadgeDrawable;

public class MainActivityUser extends AppCompatActivity {
    ActivityMainUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        BadgeDrawable badgeDrawable = binding.bottomNavigationView.getOrCreateBadge(R.id.cart_screen);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(3);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_screen:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.cart_screen:
                    replaceFragment(new CartFragment());
                    break;
                case R.id.wishlist_screen:
                    replaceFragment(new WishlistFragment());
                    break;
                case R.id.caring_screen:
                    replaceFragment(new CaringFragment());
                    break;
                case R.id.profile_screen:
                    replaceFragment(new ProfileFragment());
                    break;

            }

            return true;
        });
    }

    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}