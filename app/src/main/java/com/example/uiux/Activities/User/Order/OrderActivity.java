package com.example.uiux.Activities.User.Order;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uiux.Adapters.OrderStatusAdapter;
import com.example.uiux.Fragments.User.Order.OrderFragment;
import com.example.uiux.R;
import com.example.uiux.Utils.OrderStatus;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderActivity extends AppCompatActivity {
    TabLayout tabLayout_order;
    ViewPager2 viewPager_order;
    OrderStatusAdapter orderStatusAdapter;
    ImageView img_back_order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_order);

        initWidget();


        int targetTab = getIntent().getIntExtra("targetTab", -1);
        Log.e("Order Activity", "Chưa vào");

        if (targetTab < 0 || targetTab > OrderStatus.RETURNED) {
            targetTab = OrderStatus.PENDING;
        }
        viewPager_order.setCurrentItem(targetTab, true);


        viewPager_order.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.e("OrderActivity", "Tab được chọn - status: " + position);

                // Truyền giá trị status vào Fragment hiện tại
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + position);
                if (currentFragment instanceof OrderFragment) {
                    // Kiểm tra nếu Fragment đã được khởi tạo trước khi gọi updateStatus
                    ((OrderFragment) currentFragment).updateStatus(position);
                }
            }
        });

    }


    void initWidget() {
        img_back_order = findViewById(R.id.img_back_order);
        img_back_order.setOnClickListener(view -> finish());
        tabLayout_order = findViewById(R.id.tabLayout_order);
        viewPager_order = findViewById(R.id.viewPager_order);
        orderStatusAdapter = new OrderStatusAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager_order.setAdapter(orderStatusAdapter);
//        new TabLayoutMediator(tabLayout_order, viewPager_order, (tab, i) -> {
//            switch (i) {
//                case OrderStatus.PENDING:
//                    tab.setText(OrderStatus.getStatusName(OrderStatus.PENDING));
//                    break;
//                case OrderStatus.PREPARING:
//                    tab.setText(OrderStatus.getStatusName(OrderStatus.PREPARING));
//                    break;
//                case OrderStatus.SHIPPING:
//                    tab.setText(OrderStatus.getStatusName(OrderStatus.SHIPPING));
//                    break;
//                case OrderStatus.DELIVERED:
//                    tab.setText(OrderStatus.getStatusName(OrderStatus.DELIVERED));
//                    break;
//                case OrderStatus.CANCELED:
//                    tab.setText(OrderStatus.getStatusName(OrderStatus.CANCELED));
//                    break;
//                case OrderStatus.RETURNED:
//                    tab.setText(OrderStatus.getStatusName(OrderStatus.RETURNED));
//                    break;
//            }
//        }).attach();
        new TabLayoutMediator(tabLayout_order, viewPager_order, (tab, position) -> {
            tab.setText(OrderStatus.getStatusName(position));
        }).attach();


    }
}