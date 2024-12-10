package com.example.uiux.Utils.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.uiux.Activities.OrderDetailActivity;
import com.example.uiux.Activities.User.MainActivityUser;
import com.example.uiux.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class FCMNotificationService extends FirebaseMessagingService {
    private NotificationManager notificationManager;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

//        Map<String, String> data= message.getData();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 10, 100, 200};
        vibrator.vibrate(pattern, -1);

        Map<String, String> data = message.getData();
        String orderId = data.get("order_id");

        Intent resultIntent = new Intent(this, OrderDetailActivity.class);
        resultIntent.putExtra("order_id", orderId); // Truyền order_id qua Intent
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

//        Log.e("Title", Objects.requireNonNull(message.getNotification()).getTitle());
//        Log.e("Content", Objects.requireNonNull(message.getNotification().getBody()));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notification");


//        Intent resultIntent = new Intent(this, MainActivityUser.class);
//        PendingIntent pendingIntent;
//        pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentTitle(Objects.requireNonNull(message.getNotification()).getTitle());
        builder.setContentText(message.getNotification().getBody());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.logo_main);
        builder.setVibrate(pattern);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);


        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "Notification";
            NotificationChannel channel = new NotificationChannel(
                    channelID,"Order Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(pattern);
            channel.canBypassDnd();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.canBubble();
            }
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelID);

        }

        notificationManager.notify(100, builder.build());
    }
}
