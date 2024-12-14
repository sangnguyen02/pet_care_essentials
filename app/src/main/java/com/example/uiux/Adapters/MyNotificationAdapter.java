package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Notification;
import com.example.uiux.Model.WalletHistory;
import com.example.uiux.R;

import java.util.List;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.MyNotificationViewHolder> {

    private List<Notification> notificationList;
    private Context context;

    public MyNotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_notification, parent, false);
        return new MyNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotificationAdapter.MyNotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

     class MyNotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tv_notification_message, tv_notification_date;

        public MyNotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_notification_message = itemView.findViewById(R.id.tv_notification_message);
            tv_notification_date = itemView.findViewById(R.id.tv_notification_date);
        }
         public void bind(Notification walletHistory) {
             tv_notification_message.setText("Message: " + (walletHistory.getContent() != null ? walletHistory.getContent() : "N/A"));

             tv_notification_date.setText(walletHistory.getSent_time());

         }


     }
}
