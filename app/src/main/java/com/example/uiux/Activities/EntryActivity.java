package com.example.uiux.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uiux.Activities.Admin.MainActivityAdmin;
import com.example.uiux.Activities.User.MainActivityUser;
import com.example.uiux.Adapters.EntryAdapter;
import com.example.uiux.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class EntryActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FloatingActionButton google, guest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        initWidget();
        tabLayoutConfig();

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntryActivity.this, MainActivityUser.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void initWidget() {
        tabLayout = findViewById(R.id.tab_layout_entry);
        viewPager2 = findViewById(R.id.tab_view_pager);
        google = findViewById(R.id.fab_google);
        guest = findViewById(R.id.fab_guest);
    }

    void tabLayoutConfig() {
        tabLayout.addTab(tabLayout.newTab().setText("Sign in"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign up"));

        final EntryAdapter adapter = new EntryAdapter(this, tabLayout.getTabCount());
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }
}