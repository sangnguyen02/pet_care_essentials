package com.example.uiux.Activities.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.MyNotificationAdapter;
import com.example.uiux.Model.Notification;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    ImageView img_back_notification;
    RecyclerView rcv_my_notification;
    MyNotificationAdapter myNotificationAdapter;
    List<Notification> notificationList;
    String accountId;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_notification);
        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        initWidget();
        loadNotification();
    }

    void initWidget() {
        img_back_notification = findViewById(R.id.img_back_notification);
        img_back_notification.setOnClickListener(view -> finish());
        notificationList = new ArrayList<>();
        rcv_my_notification = findViewById(R.id.rcv_my_notification);
        rcv_my_notification.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    void loadNotification() {
        DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference("Notification");
        notiRef.orderByChild("account_id").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification notification = data.getValue(Notification.class);
                    if (notification != null) {
                        notificationList.add(notification);
                    }
                }

                if (myNotificationAdapter == null) {
                    myNotificationAdapter = new MyNotificationAdapter(notificationList, NotificationActivity.this);
                    rcv_my_notification.setAdapter(myNotificationAdapter);
                } else {
                    myNotificationAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}