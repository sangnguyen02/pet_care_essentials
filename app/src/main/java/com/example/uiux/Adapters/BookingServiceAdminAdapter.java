package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Services.DisplayServiceBookingActivity;
import com.example.uiux.Activities.OrderDetailActivity;
import com.example.uiux.Activities.User.Service.CancelServiceActivity;
import com.example.uiux.Model.Notification;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.Utils.FCM.FcmNotificationSender;
import com.example.uiux.Utils.OrderStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingServiceAdminAdapter extends RecyclerView.Adapter<BookingServiceAdminAdapter.ServiceAdminViewHolder>{
    private Context context;
    private List<ServiceOrder> serviceOrderList;
    public BookingServiceAdminAdapter(Context context, List<ServiceOrder> serviceOrderList) {
        this.context = context;
        this.serviceOrderList = serviceOrderList;
    }

    @NonNull
    @Override
    public BookingServiceAdminAdapter.ServiceAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_service, parent, false);
        return new BookingServiceAdminAdapter.ServiceAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingServiceAdminAdapter.ServiceAdminViewHolder holder, int position) {
        ServiceOrder serviceOrder = serviceOrderList.get(position);

        // Bind data to the views
        // Bind data from ServiceOrder to the views in the ViewHolder
        holder.tvServiceName.setText(serviceOrder.getService_name());
        holder.tvServiceType.setText(serviceOrder.getType());
        holder.tvOrderDate.setText(serviceOrder.getOrder_date());
        holder.tvTotalPrice.setText(CurrencyFormatter.formatCurrency(serviceOrder.getTotal_price(), holder.itemView.getContext().getString(R.string.currency_vn)));
        holder.tvBranchName.setText(serviceOrder.getBranch_name());
        holder.tvBranchAddress.setText(serviceOrder.getBranch_address());
        holder.tvTime.setText(serviceOrder.getTime());
        holder.tvPhone.setText(serviceOrder.getPhone_number());
        holder.tvMail.setText(serviceOrder.getEmail());
        holder.tvName.setText(serviceOrder.getName());

        holder.itemView.setOnClickListener(view -> {
            // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
            new AlertDialog.Builder(context)
                    .setItems(new CharSequence[]{"Notify"}, (dialog, which) -> {
                        if (which == 0) {
                            DatabaseReference userFCMTokenRef = FirebaseDatabase.getInstance()
                                    .getReference("Account")
                                    .child(serviceOrder.getAccount_id())
                                    .child("fcm_token");
                            userFCMTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String userToken = snapshot.getValue(String.class);
                                        String title = "Service Booking: " + serviceOrder.getService_name();
                                        String body = "Your service booking is scheduled for " + serviceOrder.getTime() + ".";

                                        FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                                userToken,
                                                title,
                                                body,
                                                null,
                                                holder.itemView.getContext());
                                        fcmNotificationSender.sendNotification();

//                                Log.e("User FCM TOken", userToken);

                                        createNotification(serviceOrder.getAccount_id(), body);
//                                        finish();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(holder.itemView.getContext(), "Failed to fetch user token: " + error.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .show();
        });



    }
    private void createNotification(String accountId, String content) {
        DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("Notification");
        String notificationId = notifyRef.push().getKey();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        Notification notification = new Notification(
                notificationId,
                accountId,
                false,
                content,
                0,
                2,
                currentTime
        );

        if (notificationId != null) {
            notifyRef.child(notificationId).setValue(notification).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.e("Notification","Notification created successfully.");
                } else {
                    Log.e("Notification","Failed to create notification.");
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return serviceOrderList.size();
    }
    public class ServiceAdminViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvServiceName, tvServiceType, tvOrderDate, tvTotalPrice, tvBranchName, tvBranchAddress, tvTime,tvPhone,tvMail,tvName;

        public ServiceAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceType = itemView.findViewById(R.id.tv_service_type);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvBranchName = itemView.findViewById(R.id.tv_branch_name);
            tvBranchAddress = itemView.findViewById(R.id.tv_branch_address);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvMail = itemView.findViewById(R.id.tv_email);
            tvName = itemView.findViewById(R.id.tv_username);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//                    ServiceOrder serviceOrder = serviceOrderList.get(position);
//                    Intent intent = new Intent(context, DisplayServiceBookingActivity.class);
//                    intent.putExtra("orderServiceId",serviceOrder.getService_order_id());
//                    intent.putExtra("service_id", serviceOrder.getService_id());
//                    intent.putExtra("service_name", serviceOrder.getService_name());
//                    context.startActivity(intent);
//                }
//            });

        }
    }
}
