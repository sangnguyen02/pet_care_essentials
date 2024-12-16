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

import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticAllRevenueActivity extends AppCompatActivity {

    private  DatabaseReference orderRef;
    private  DatabaseReference supplies_importRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistic_all_revenue);
        GetTotalPriceOfOrder();
        GetTotalPriceOfOrderByDateRange("2024-12-10 00:00:00", "2024-12-14 23:59:59");
        GetTotalPriceOfSupplyImport();
        GetTotalPriceOfSupplyImportByDateRange("09:00 10/12/2024", "23:59 14/12/2024");

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

private void GetOrdersByCategory(String categoryFilter) {
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
    DatabaseReference suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies");

    // Lấy dữ liệu đơn hàng
    orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            List<Order> filteredOrders = new ArrayList<>();

            // Duyệt qua tất cả các order
            for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                Order order = orderSnapshot.getValue(Order.class);
                if (order != null && order.getCart_items_ordered() != null) {
                    // Duyệt qua các CartItem trong mỗi Order
                    for (CartItem cartItem : order.getCart_items_ordered()) {
                        String supplyId = cartItem.getSupply_id();

                        // Lấy category của từng Supply
                        suppliesRef.child(supplyId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot supplySnapshot) {
                                Supplies supply = supplySnapshot.getValue(Supplies.class);
                                if (supply != null && supply.getCategory() != null) {
                                    // Kiểm tra xem category của supply có trùng với categoryFilter không
                                    if (supply.getCategory().equals(categoryFilter)) {
                                        // Nếu trùng, thêm Order vào danh sách kết quả
                                        if (!filteredOrders.contains(order)) {
                                            filteredOrders.add(order);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", "Failed to read supply category: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            // Hiển thị hoặc xử lý danh sách Order đã lọc
            Log.d("FilteredOrders", "Filtered Orders by Category: " + filteredOrders.size());
            Toast.makeText(StatisticAllRevenueActivity.this,
                    "Filtered Orders: " + filteredOrders.size(), Toast.LENGTH_LONG).show();
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
    private void GetTotalPriceOfSupplyImport() {
        supplies_importRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");

        supplies_importRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalPrice = 0.0;

                for (DataSnapshot importSnapshot : snapshot.getChildren()) {
                    // Ánh xạ dữ liệu Firebase thành đối tượng Supplies_Import
                    Supplies_Import suppliesImport = importSnapshot.getValue(Supplies_Import.class);

                    if (suppliesImport != null && suppliesImport.getSuppliesDetail() != null) {
                        for (Supplies_Detail detail : suppliesImport.getSuppliesDetail()) {
                            totalPrice += detail.getQuantity() * detail.getImport_price(); // Cộng giá trị từng size
                        }
                    }
                }

                // Hiển thị hoặc xử lý tổng giá trị supplies imports
                Log.d("TotalPrice", "Total Price of Supplies Imports: " + totalPrice);
                Toast.makeText(StatisticAllRevenueActivity.this,
                        "Total Price of Supplies Imports: " + totalPrice, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read supplies imports: " + error.getMessage());
            }
        });
    }
    private void GetTotalPriceOfSupplyImportByDateRange(String startDate, String endDate) {
        supplies_importRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        try {
            // Parse startDate và endDate thành đối tượng Date
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            supplies_importRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalPrice = 0.0;

                    for (DataSnapshot importSnapshot : snapshot.getChildren()) {
                        // Ánh xạ dữ liệu Firebase vào đối tượng Supplies_Import
                        Supplies_Import suppliesImport = importSnapshot.getValue(Supplies_Import.class);

                        if (suppliesImport != null && suppliesImport.getImport_date() != null) {
                            String importDateStr = suppliesImport.getImport_date().trim(); // Xử lý trường hợp chuỗi thừa dấu cách

                            if (!importDateStr.isEmpty()) { // Kiểm tra chuỗi không rỗng
                                try {
                                    // Parse import_date từ chuỗi sang Date
                                    Log.e("Day", importDateStr);
                                    Date importDate = dateFormat.parse(importDateStr);

                                    // Kiểm tra nếu importDate nằm trong khoảng [start, end]
                                    if (importDate != null && !importDate.before(start) && !importDate.after(end)) {
                                        // Tính tổng giá trị cho từng size trong danh sách sizes
                                        if (suppliesImport.getSuppliesDetail() != null) {
                                            for (Supplies_Detail detail : suppliesImport.getSuppliesDetail()) {
                                                totalPrice += detail.getQuantity() * detail.getImport_price();
                                            }
                                        }
                                    }
                                } catch (ParseException e) {
                                    Log.e("DateParseError", "Error parsing import date: " + importDateStr, e);
                                }
                            } else {
                                Log.e("DataError", "Import date is empty for snapshot: " + importSnapshot.getKey());
                            }
                        } else {
                            Log.e("DataError", "Supplies_Import or import_date is null for snapshot: " + importSnapshot.getKey());
                        }
                    }

                    // Hiển thị hoặc xử lý tổng giá trị supplies imports trong khoảng thời gian
                    Log.d("TotalPrice", "Total Price of Supplies Imports from " + startDate + " to " + endDate + ": " + totalPrice);
                    Toast.makeText(StatisticAllRevenueActivity.this,
                            "Total Supplies Import Price from " + startDate + " to " + endDate + ": " + totalPrice, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Failed to read supplies imports: " + error.getMessage());
                }
            });

        } catch (ParseException e) {
            Log.e("DateParseError", "Error parsing start or end date", e);
        }
    }








}