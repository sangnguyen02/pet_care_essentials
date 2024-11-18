package com.example.uiux.Activities.User.Order;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uiux.Adapters.OrderStatusAdapter;
import com.example.uiux.R;
import com.example.uiux.Utils.OrderStatus;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderActivity extends AppCompatActivity {
    TabLayout tabLayout_order;
    ViewPager2 viewPager_order;
    OrderStatusAdapter orderStatusAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_order);

        initWidget();

        int targetTab = getIntent().getIntExtra("targetTab", -1);

        if (targetTab < 0 || targetTab > OrderStatus.RETURNED) {
            targetTab = OrderStatus.PENDING;
        }

        viewPager_order.setCurrentItem(targetTab, true);


    }


    void initWidget() {
        tabLayout_order = findViewById(R.id.tabLayout_order);
        viewPager_order = findViewById(R.id.viewPager_order);
        orderStatusAdapter = new OrderStatusAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager_order.setAdapter(orderStatusAdapter);

        new TabLayoutMediator(tabLayout_order, viewPager_order, (tab, position) -> {
            tab.setText(OrderStatus.getStatusName(position));
        }).attach();
    }
}