package com.example.uiux.Activities.Admin.Statistic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.Utils.OrderStatus;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//public class StatisticAllRevenueActivity extends AppCompatActivity {
//
//    private  DatabaseReference orderRef;
//    private  DatabaseReference supplies_importRef;
//    private LineChart lineChartRevenue;
//    private BarChart barChartImport;
//    private TextView tvTotalRevenue;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
//        setContentView(R.layout.activity_statistic_all_revenue);
////        GetTotalPriceOfOrder();
////        GetTotalPriceOfOrderByDateRange("2024-12-10 00:00:00", "2024-12-14 23:59:59");
////        GetTotalPriceOfSupplyImport();
////        GetTotalPriceOfSupplyImportByDateRange("09:00 10/12/2024", "23:59 14/12/2024");
//        lineChartRevenue = findViewById(R.id.lineChartRevenue);
//        barChartImport = findViewById(R.id.barChartImport);
//        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
//
//        // Vẽ biểu đồ và tính toán dữ liệu
//        loadOrderRevenueData();
//        loadSupplyImportData();
//
//    }
//    private void loadOrderRevenueData() {
//        orderRef = FirebaseDatabase.getInstance().getReference("Order");
//
//        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Entry> lineEntries = new ArrayList<>();
//                double totalRevenue = 0.0;
//
//                int index = 0;
//                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
//                    String dateStr = orderSnapshot.child("date_order").getValue(String.class);
//                    Double totalPrice = orderSnapshot.child("total_price").getValue(Double.class);
//
//                    if (dateStr != null && totalPrice != null) {
//                        totalRevenue += totalPrice;
//                        lineEntries.add(new Entry(index++, totalPrice.floatValue()));
//                    }
//                }
//
//                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Daily Revenue");
//                lineDataSet.setColor(Color.BLUE);
//                lineDataSet.setValueTextColor(Color.BLACK);
//
//                LineData lineData = new LineData(lineDataSet);
//                lineChartRevenue.setData(lineData);
//                lineChartRevenue.getDescription().setText("Revenue Trend");
//                lineChartRevenue.invalidate();
//
//                // Hiển thị tổng doanh thu
//                tvTotalRevenue.setText("Total Revenue: $" + totalRevenue);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to read order revenue: " + error.getMessage());
//            }
//        });
//    }
//
//    private void loadSupplyImportData() {
//        supplies_importRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
//
//        supplies_importRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<BarEntry> barEntries = new ArrayList<>();
//                int index = 0;
//
//                for (DataSnapshot importSnapshot : snapshot.getChildren()) {
//                    Supplies_Import suppliesImport = importSnapshot.getValue(Supplies_Import.class);
//                    if (suppliesImport != null && suppliesImport.getSuppliesDetail() != null) {
//                        double totalImportPrice = 0.0;
//                        for (Supplies_Detail detail : suppliesImport.getSuppliesDetail()) {
//                            totalImportPrice += detail.getQuantity() * detail.getImport_price();
//                        }
//                        barEntries.add(new BarEntry(index++, (float) totalImportPrice));
//                    }
//                }
//
//                BarDataSet barDataSet = new BarDataSet(barEntries, "Supply Imports");
//                barDataSet.setColor(Color.MAGENTA);
//
//                BarData barData = new BarData(barDataSet);
//                barChartImport.setData(barData);
//                barChartImport.getDescription().setText("Supply Imports Cost");
//                barChartImport.invalidate();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to read supply imports: " + error.getMessage());
//            }
//        });
//    }
//    private void GetTotalPriceOfOrder() {
//        orderRef = FirebaseDatabase.getInstance().getReference("Order");
//        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                double totalPrice = 0.0;
//
//                // Duyệt qua tất cả các order trong Firebase
//                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
//                    // Lấy giá trị total_price của từng order
//                    Double orderPrice = orderSnapshot.child("total_price").getValue(Double.class);
//
//                    if (orderPrice != null) {
//                        totalPrice += orderPrice; // Cộng vào tổng
//                    }
//                }
//
//                // Hiển thị hoặc xử lý tổng total_price
//                Log.d("TotalPrice", "Total Price of Orders: " + totalPrice);
//                Toast.makeText(StatisticAllRevenueActivity.this,
//                        "Total Price of Orders: " + totalPrice, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to read orders: " + error.getMessage());
//            }
//        });
//    }
//
//    private void GetOrdersByCategory(String categoryFilter) {
//    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
//    DatabaseReference suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies");
//
//    // Lấy dữ liệu đơn hàng
//    orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            List<Order> filteredOrders = new ArrayList<>();
//
//            // Duyệt qua tất cả các order
//            for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
//                Order order = orderSnapshot.getValue(Order.class);
//                if (order != null && order.getCart_items_ordered() != null) {
//                    // Duyệt qua các CartItem trong mỗi Order
//                    for (CartItem cartItem : order.getCart_items_ordered()) {
//                        String supplyId = cartItem.getSupply_id();
//
//                        // Lấy category của từng Supply
//                        suppliesRef.child(supplyId).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot supplySnapshot) {
//                                Supplies supply = supplySnapshot.getValue(Supplies.class);
//                                if (supply != null && supply.getCategory() != null) {
//                                    // Kiểm tra xem category của supply có trùng với categoryFilter không
//                                    if (supply.getCategory().equals(categoryFilter)) {
//                                        // Nếu trùng, thêm Order vào danh sách kết quả
//                                        if (!filteredOrders.contains(order)) {
//                                            filteredOrders.add(order);
//                                        }
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.e("FirebaseError", "Failed to read supply category: " + error.getMessage());
//                            }
//                        });
//                    }
//                }
//            }
//
//            // Hiển thị hoặc xử lý danh sách Order đã lọc
//            Log.d("FilteredOrders", "Filtered Orders by Category: " + filteredOrders.size());
//            Toast.makeText(StatisticAllRevenueActivity.this,
//                    "Filtered Orders: " + filteredOrders.size(), Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//            Log.e("FirebaseError", "Failed to read orders: " + error.getMessage());
//        }
//    });
//}
//
//    //truyen 2 tham so date de loc theo thoi gian chu y forrmat
//    private void GetTotalPriceOfOrderByDateRange(String startDate, String endDate) {
//        orderRef = FirebaseDatabase.getInstance().getReference("Order");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//
//        try {
//            // Chuyển startDate và endDate thành đối tượng Date
//            Date start = dateFormat.parse(startDate);
//            Date end = dateFormat.parse(endDate);
//
//            orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    double totalPrice = 0.0;
//
//                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
//                        String orderDateStr = orderSnapshot.child("date_order").getValue(String.class);
//                        Double orderPrice = orderSnapshot.child("total_price").getValue(Double.class);
//
//                        if (orderDateStr != null && orderPrice != null) {
//                            try {
//                                // Chuyển date_order từ chuỗi thành đối tượng Date
//                                Date orderDate = dateFormat.parse(orderDateStr);
//
//                                // Kiểm tra nếu orderDate nằm trong khoảng [start, end]
//                                if (orderDate != null && !orderDate.before(start) && !orderDate.after(end)) {
//                                    totalPrice += orderPrice;
//                                }
//                            } catch (ParseException e) {
//                                Log.e("DateParseError", "Error parsing order date: " + orderDateStr, e);
//                            }
//                        }
//                    }
//
//                    // Hiển thị hoặc xử lý tổng total_price trong khoảng thời gian
//                    Log.d("TotalPrice", "Total Price of Orders from " + startDate + " to " + endDate + ": " + totalPrice);
//                    Toast.makeText(StatisticAllRevenueActivity.this,
//                            "Total Price from " + startDate + " to " + endDate + ": " + totalPrice, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e("FirebaseError", "Failed to read orders: " + error.getMessage());
//                }
//            });
//
//        } catch (ParseException e) {
//            Log.e("DateParseError", "Error parsing start or end date", e);
//        }
//    }
//    private void GetTotalPriceOfSupplyImport() {
//        supplies_importRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
//
//        supplies_importRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                double totalPrice = 0.0;
//
//                for (DataSnapshot importSnapshot : snapshot.getChildren()) {
//                    // Ánh xạ dữ liệu Firebase thành đối tượng Supplies_Import
//                    Supplies_Import suppliesImport = importSnapshot.getValue(Supplies_Import.class);
//
//                    if (suppliesImport != null && suppliesImport.getSuppliesDetail() != null) {
//                        for (Supplies_Detail detail : suppliesImport.getSuppliesDetail()) {
//                            totalPrice += detail.getQuantity() * detail.getImport_price(); // Cộng giá trị từng size
//                        }
//                    }
//                }
//
//                // Hiển thị hoặc xử lý tổng giá trị supplies imports
//                Log.d("TotalPrice", "Total Price of Supplies Imports: " + totalPrice);
//                Toast.makeText(StatisticAllRevenueActivity.this,
//                        "Total Price of Supplies Imports: " + totalPrice, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to read supplies imports: " + error.getMessage());
//            }
//        });
//    }
//    private void GetTotalPriceOfSupplyImportByDateRange(String startDate, String endDate) {
//        supplies_importRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
//
//        try {
//            // Parse startDate và endDate thành đối tượng Date
//            Date start = dateFormat.parse(startDate);
//            Date end = dateFormat.parse(endDate);
//
//            supplies_importRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    double totalPrice = 0.0;
//
//                    for (DataSnapshot importSnapshot : snapshot.getChildren()) {
//                        // Ánh xạ dữ liệu Firebase vào đối tượng Supplies_Import
//                        Supplies_Import suppliesImport = importSnapshot.getValue(Supplies_Import.class);
//
//                        if (suppliesImport != null && suppliesImport.getImport_date() != null) {
//                            String importDateStr = suppliesImport.getImport_date().trim(); // Xử lý trường hợp chuỗi thừa dấu cách
//
//                            if (!importDateStr.isEmpty()) { // Kiểm tra chuỗi không rỗng
//                                try {
//                                    // Parse import_date từ chuỗi sang Date
//                                    Log.e("Day", importDateStr);
//                                    Date importDate = dateFormat.parse(importDateStr);
//
//                                    // Kiểm tra nếu importDate nằm trong khoảng [start, end]
//                                    if (importDate != null && !importDate.before(start) && !importDate.after(end)) {
//                                        // Tính tổng giá trị cho từng size trong danh sách sizes
//                                        if (suppliesImport.getSuppliesDetail() != null) {
//                                            for (Supplies_Detail detail : suppliesImport.getSuppliesDetail()) {
//                                                totalPrice += detail.getQuantity() * detail.getImport_price();
//                                            }
//                                        }
//                                    }
//                                } catch (ParseException e) {
//                                    Log.e("DateParseError", "Error parsing import date: " + importDateStr, e);
//                                }
//                            } else {
//                                Log.e("DataError", "Import date is empty for snapshot: " + importSnapshot.getKey());
//                            }
//                        } else {
//                            Log.e("DataError", "Supplies_Import or import_date is null for snapshot: " + importSnapshot.getKey());
//                        }
//                    }
//
//                    // Hiển thị hoặc xử lý tổng giá trị supplies imports trong khoảng thời gian
//                    Log.d("TotalPrice", "Total Price of Supplies Imports from " + startDate + " to " + endDate + ": " + totalPrice);
//                    Toast.makeText(StatisticAllRevenueActivity.this,
//                            "Total Supplies Import Price from " + startDate + " to " + endDate + ": " + totalPrice, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e("FirebaseError", "Failed to read supplies imports: " + error.getMessage());
//                }
//            });
//
//        } catch (ParseException e) {
//            Log.e("DateParseError", "Error parsing start or end date", e);
//        }
//    }
//
//
//
//
//
//
//
//
//}

public class StatisticAllRevenueActivity extends AppCompatActivity {
    private LineChart lineChartRevenue, lineChartProfit;
    private BarChart barChartImport, barChartTopProducts;
    private PieChart pieChartOrderStatus;
    private DatePicker startDatePicker, endDatePicker;
    private TextView tvTotalRevenue, tvTotalProfit;

    private DatabaseReference orderRef, supplyImportRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_all_revenue);

        // Ánh xạ các view
        lineChartRevenue = findViewById(R.id.lineChartRevenue);
        barChartImport = findViewById(R.id.barChartImport);
        lineChartProfit = findViewById(R.id.lineChartProfit);
        barChartTopProducts = findViewById(R.id.barChartTopProducts);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalProfit = findViewById(R.id.tvTotalProfit);

//        startDatePicker = findViewById(R.id.datePickerStart);
//        endDatePicker = findViewById(R.id.datePickerEnd);

        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        supplyImportRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");

        // Load các thống kê
        loadRevenueData();
        loadImportCostData();
        loadTopSellingProducts();
        loadProfitData();

//        startDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> updateData());
//        endDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> updateData());
    }

    private void loadRevenueData() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> revenueEntries = new ArrayList<>();
                float totalRevenue = 0f;
                int index = 0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Float price = orderSnapshot.child("total_price").getValue(Float.class);
                    if (price != null) {
                        revenueEntries.add(new Entry(index++, price));
                        totalRevenue += price;
                    }
                }

                LineDataSet dataSet = new LineDataSet(revenueEntries, "Daily Revenue");
                dataSet.setColor(Color.BLUE);
                lineChartRevenue.setData(new LineData(dataSet));
                lineChartRevenue.invalidate();

                tvTotalRevenue.setText("Total Revenue: " + CurrencyFormatter.formatCurrency(totalRevenue, getString(R.string.currency_vn)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }

    private void loadImportCostData() {
        supplyImportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();
                int index = 0;

                for (DataSnapshot importSnapshot : snapshot.getChildren()) {
                    double totalCost = 0.0;
                    for (DataSnapshot detailSnapshot : importSnapshot.child("suppliesDetail").getChildren()) {
                        double price = detailSnapshot.child("import_price").getValue(Double.class);
                        int quantity = detailSnapshot.child("quantity").getValue(Integer.class);
                        totalCost += price * quantity;
                    }
                    entries.add(new BarEntry(index++, (float) totalCost));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Import Costs");
                barChartImport.setData(new BarData(dataSet));
                barChartImport.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }




    private void loadTopSellingProducts() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Map để lưu tổng số lượng bán ra của từng sản phẩm
                Map<String, Integer> productSalesCount = new HashMap<>();

                // Duyệt qua từng đơn hàng
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class); // Parse thành Order

                    if (order != null && order.getCart_items_ordered() != null) {
                        for (CartItem cartItem : order.getCart_items_ordered()) {
                            String productName = cartItem.getSupply_title();
                            int quantity = cartItem.getQuantity();

                            if (productName != null) {
                                productSalesCount.put(productName,
                                        productSalesCount.getOrDefault(productName, 0) + quantity);
                            }
                        }
                    }
                }

                // Chuyển dữ liệu từ Map sang BarChart
                List<BarEntry> entries = new ArrayList<>();
                List<String> productNames = new ArrayList<>();
                int index = 0;

                for (Map.Entry<String, Integer> entry : productSalesCount.entrySet()) {
                    entries.add(new BarEntry(index, entry.getValue()));
                    productNames.add(entry.getKey());
                    index++;
                }

                // Tạo và thiết lập BarDataSet
                BarDataSet dataSet = new BarDataSet(entries, "Top Selling Products");
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(12f);

                // Gắn dữ liệu vào BarChart
                BarData barData = new BarData(dataSet);
                barChartTopProducts.setData(barData);
                barChartTopProducts.getDescription().setEnabled(false);
                barChartTopProducts.getXAxis().setValueFormatter(new IndexAxisValueFormatter(productNames));
                barChartTopProducts.getXAxis().setGranularity(1f); // Đảm bảo mỗi nhãn chỉ xuất hiện một lần
                barChartTopProducts.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Đưa trục X xuống dưới

                barChartTopProducts.getXAxis().setGranularity(1f); // Đảm bảo trục X không bị trùng lặp
                barChartTopProducts.invalidate(); // Làm mới biểu đồ
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load orders: " + error.getMessage());
            }
        });
    }

    private void loadProfitData() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> profitEntries = new ArrayList<>();
                final float[] totalProfit = {0f}; // Dùng mảng để thay đổi giá trị
                final int[] index = {0}; // Sử dụng mảng cho index

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Float price = orderSnapshot.child("total_price").getValue(Float.class);  // Lấy giá bán
                    if (price != null) {
                        supplyImportRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot supplySnapshot) {
                                double totalCost = 0.0;

                                // Lấy chi phí nhập từ dữ liệu Supplies_Import
                                for (DataSnapshot importSnapshot : supplySnapshot.getChildren()) {
                                    for (DataSnapshot detailSnapshot : importSnapshot.child("suppliesDetail").getChildren()) {
                                        double importPrice = detailSnapshot.child("import_price").getValue(Double.class);
                                        int quantity = detailSnapshot.child("quantity").getValue(Integer.class);
                                        totalCost += importPrice * quantity;  // Tính tổng chi phí nhập
                                    }
                                }

                                // Tính lợi nhuận
                                float profit = price - (float) totalCost;
                                profitEntries.add(new Entry(index[0]++, profit));  // Tăng index và thêm vào Entry

                                totalProfit[0] += profit; // Cập nhật tổng lợi nhuận trong mảng

                                // Cập nhật biểu đồ và tổng lợi nhuận
                                LineDataSet dataSet = new LineDataSet(profitEntries, "Profit Data");
                                dataSet.setColor(Color.GREEN);
                                lineChartProfit.setData(new LineData(dataSet));
                                lineChartProfit.invalidate();



                                // Cập nhật tổng lợi nhuận trên TextView
                                // Cập nhật tổng lợi nhuận trên TextView
                                tvTotalProfit.setText("Total Profit: " + CurrencyFormatter.formatCurrency(totalProfit[0], getString(R.string.currency_vn)));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }



}