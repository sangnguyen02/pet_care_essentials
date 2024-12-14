package com.example.uiux.Activities.Admin.Statistic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatisticAllRevenueActivity extends AppCompatActivity {

    private  DatabaseReference orderRef;
    private  DatabaseReference walletHistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistic_all_revenue);
        GetTotalPriceOfOrder();
        GetTotalPriceOfOrderByDateRange("2024-12-10 00:00:00", "2024-12-14 23:59:59");

    }
    private void GetTotalPriceOfOrder() {
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalPrice = 0.0;

                // Duyệt qua tất cả các order trong Firebase
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    // Lấy giá trị total_price của từng order
                    Double orderPrice = orderSnapshot.child("total_price").getValue(Double.class);

                    if (orderPrice != null) {
                        totalPrice += orderPrice; // Cộng vào tổng
                    }
                }

                // Hiển thị hoặc xử lý tổng total_price
                Log.d("TotalPrice", "Total Price of Orders: " + totalPrice);
                Toast.makeText(StatisticAllRevenueActivity.this,
                        "Total Price of Orders: " + totalPrice, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read orders: " + error.getMessage());
            }
        });
    }

    //truyen 2 tham so date de loc theo thoi gian chu y forrmat
    private void GetTotalPriceOfOrderByDateRange(String startDate, String endDate) {
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            // Chuyển startDate và endDate thành đối tượng Date
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalPrice = 0.0;

                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        String orderDateStr = orderSnapshot.child("date_order").getValue(String.class);
                        Double orderPrice = orderSnapshot.child("total_price").getValue(Double.class);

                        if (orderDateStr != null && orderPrice != null) {
                            try {
                                // Chuyển date_order từ chuỗi thành đối tượng Date
                                Date orderDate = dateFormat.parse(orderDateStr);

                                // Kiểm tra nếu orderDate nằm trong khoảng [start, end]
                                if (orderDate != null && !orderDate.before(start) && !orderDate.after(end)) {
                                    totalPrice += orderPrice;
                                }
                            } catch (ParseException e) {
                                Log.e("DateParseError", "Error parsing order date: " + orderDateStr, e);
                            }
                        }
                    }

                    // Hiển thị hoặc xử lý tổng total_price trong khoảng thời gian
                    Log.d("TotalPrice", "Total Price of Orders from " + startDate + " to " + endDate + ": " + totalPrice);
                    Toast.makeText(StatisticAllRevenueActivity.this,
                            "Total Price from " + startDate + " to " + endDate + ": " + totalPrice, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Failed to read orders: " + error.getMessage());
                }
            });

        } catch (ParseException e) {
            Log.e("DateParseError", "Error parsing start or end date", e);
        }
    }



}