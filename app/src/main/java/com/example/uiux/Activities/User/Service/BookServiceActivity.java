package com.example.uiux.Activities.User.Service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.Map.BranchMapActivity;
import com.example.uiux.Activities.User.PaymentActivity;
import com.example.uiux.Activities.User.Profile.UpdateAddressActivity;
import com.example.uiux.Adapters.DayAdapter;
import com.example.uiux.Adapters.ServiceUserAdapter;
import com.example.uiux.Adapters.TimeAdapter;
import com.example.uiux.Fragments.User.CaringFragment;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Service;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.Utils.OrderServiceStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookServiceActivity extends AppCompatActivity {

    RecyclerView rcv_days, rcv_time_slots;
    DayAdapter dayAdapter;
    TimeAdapter timeAdapter;
    List<Calendar> times;
    List<Calendar> days;
    ImageView img_back_booking, img_service_chosen;
    TextView tv_service_name_chosen, tv_service_price_chosen, tv_service_branch_name, tv_service_branch_address;
    String serviceId;
    MaterialCardView mcv_service_branch;
    MaterialButton btn_confirm_book;
    String accountId;
    String branch_id;

    String branch_name;
    String branch_address;

    String email;
    String phone_number;
    String name;
    String type;
    String service_id;
    String service_name;
    double total_price;
    String timeSlot = "";
    String order_date="";

    private ActivityResultLauncher<Intent> branchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selectedBranch = data.getStringExtra("selected_branch");

                        Log.e("ID ",selectedBranch);

                        if(selectedBranch != null) {
                            loadBranch(selectedBranch);
                        }

                    }
                    else
                    {
                        Log.e("CHeck null","NUll");
                    }
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_book_service);
        serviceId = getIntent().getStringExtra("serviceId");
        initWidget();

        if (serviceId != null) {
            loadServices();
        }

        loadDays();

//        Date dateOrder = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        String formattedDate = dateFormat.format(dateOrder);
        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);
        getAccountInfo();
        btn_confirm_book.setOnClickListener(view -> {
                timeSlot=timeAdapter.getSelectedTimeSlot();
            order_date=dayAdapter.getSelectedDay();
                createServiceOrder();
        });

    }

    private void getAccountInfo() {
        if (accountId == null || accountId.isEmpty()) {
            Log.e("getAccountInfo", "Account ID is null or empty");
            return;
        }

        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("Account").child(accountId);
        accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming the Account model class is set up correctly
                    Model.Account account = dataSnapshot.getValue(Model.Account.class);
                    if (account != null) {
                       email=account.getEmail();
                       name=account.getFullname();
                       phone_number=account.getPhone();
                    } else {
                        Log.e("getAccountInfo", "Account data is null");
                    }
                } else {
                    Log.e("getAccountInfo", "Account not found for ID: " + accountId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getAccountInfo", "Error fetching account info: " + databaseError.getMessage());
            }
        });
    }

    private void createServiceOrder() {
        // 1. Lấy dữ liệu từ UI và các biến đã có trong class
        String orderId = FirebaseDatabase.getInstance().getReference("Service Order").push().getKey(); // Tạo ID đơn hàng tự động




        // 4. Lấy các thông tin khác từ UI và các biến đã lưu trong class
        double price = total_price; // Hoặc bạn có thể tính toán lại nếu cần

        // 5. Tạo đối tượng ServiceOrder
        ServiceOrder serviceOrder = new ServiceOrder(
                orderId,
                service_id,
                accountId,
                service_name,
                type, // Nếu cần thêm type, đảm bảo bạn đã có nó trong Service model
                order_date,
                price,
                name,
                phone_number,
                email,
                branch_id,
                branch_name,
                branch_address,
                timeSlot,
                OrderServiceStatus.BOOK
        );

        // 6. Lưu thông tin đơn hàng vào Firebase
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Service Order").child(orderId);
        orderRef.setValue(serviceOrder).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Đơn hàng đã được tạo thành công
                Log.e("createServiceOrder", "Order created successfully");
                // Có thể chuyển hướng người dùng đến màn hình xác nhận hoặc thông báo thành công
            } else {
                // Xử lý lỗi nếu không thể lưu
                Log.e("createServiceOrder", "Failed to create order: " + task.getException());
            }
        });
    }


    void initWidget() {
        img_back_booking = findViewById(R.id.img_back_booking);
        img_back_booking.setOnClickListener(view -> finish());
        img_service_chosen = findViewById(R.id.img_service_chosen);
        tv_service_name_chosen = findViewById(R.id.tv_service_name_chosen);
        tv_service_price_chosen = findViewById(R.id.tv_service_price_chosen);

        rcv_days = findViewById(R.id.rcv_days);
        rcv_time_slots = findViewById(R.id.rcv_time_slots);

        tv_service_branch_name = findViewById(R.id.tv_service_branch_name);
        tv_service_branch_address = findViewById(R.id.tv_service_branch_address);

        mcv_service_branch = findViewById(R.id.mcv_service_branch);
        mcv_service_branch.setOnClickListener(view -> {
            Intent gotoMap = new Intent(BookServiceActivity.this, BranchMapActivity.class);
            gotoMap.putExtra("from_book_activity", true);
            branchLauncher.launch(gotoMap);
        });


        btn_confirm_book = findViewById(R.id.btn_confirm_book);

    }

    void loadServices() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Service").child(serviceId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Service service = dataSnapshot.getValue(Service.class);
                if (service != null) {
                    if (service.getImageUrls() != null && !service.getImageUrls().isEmpty()) {
                        Glide.with(BookServiceActivity.this)
                                .load(service.getImageUrls().get(0))
                                .placeholder(R.drawable.product_sample)
                                .error(R.drawable.product_sample)
                                .into(img_service_chosen);
                    }


                    tv_service_name_chosen.setText(service.getName());
                    service_id=service.getService_id();
                    service_name=service.getName();
                    type=service.getType();
                    total_price=service.getSell_price();
                    tv_service_price_chosen.setText(CurrencyFormatter.formatCurrency(service.getSell_price(), getString(R.string.currency_vn)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadDays() {
        days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 3; i++) {
            Calendar day = (Calendar) calendar.clone();
            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        setupDayRecyclerView();

        if (!days.isEmpty()) {
            loadTimeSlotsForDay(days.get(0));
        }
    }

    void setupDayRecyclerView() {
        dayAdapter = new DayAdapter(days);
        rcv_days.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Dạng ngang
        rcv_days.setAdapter(dayAdapter);

        dayAdapter.setOnItemClickListener(position -> {
            loadTimeSlotsForDay(days.get(position));
        });
    }

    void loadTimeSlotsForDay(Calendar selectedDay) {
        times = new ArrayList<>();
        Calendar timeCalendar = (Calendar) selectedDay.clone();
        timeCalendar.set(Calendar.HOUR_OF_DAY, 8);

        for (int i = 0; i < 8; i++) {
            times.add((Calendar) timeCalendar.clone());
            timeCalendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        setupTimeRecyclerView();
    }

    void setupTimeRecyclerView() {
        timeAdapter = new TimeAdapter(times);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcv_time_slots.setLayoutManager(gridLayoutManager);
        rcv_time_slots.setAdapter(timeAdapter);
    }

    void loadBranch(String branchId) {
        DatabaseReference branchRef = FirebaseDatabase.getInstance().getReference("Branch Store").child(branchId);
        branchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BranchStore branchStore = snapshot.getValue(BranchStore.class);
                if (branchStore != null) {
                    tv_service_branch_name.setText(branchStore.getBranch_name());
                    tv_service_branch_address.setText(branchStore.getAddress_details());
                    branch_id=branchId;
                    branch_name=branchStore.getBranch_name();
                    branch_address=branchStore.getAddress_details();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load branch data: " + error.getMessage());
            }
        });
    }
}