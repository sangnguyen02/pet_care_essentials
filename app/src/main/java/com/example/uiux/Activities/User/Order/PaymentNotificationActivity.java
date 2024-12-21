package com.example.uiux.Activities.User.Order;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.User.CartActivity;
import com.example.uiux.R;

public class PaymentNotificationActivity extends AppCompatActivity {
    TextView txtNotification, text_return_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_notification);
        text_return_cart = findViewById(R.id.text_return_cart);
        txtNotification = findViewById(R.id.textViewNotify);

        Intent intent = getIntent();
        txtNotification.setText(intent.getStringExtra("result"));

        int waitTimeInMillis = 3000;

        new CountDownTimer(waitTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) millisUntilFinished / 1000;
                text_return_cart.setText("Returning to cart in " + secondsRemaining + " seconds...");
            }

            @Override
            public void onFinish() {
                Intent cartIntent = new Intent(PaymentNotificationActivity.this, CartActivity.class);
                startActivity(cartIntent);
                finish();
            }
        }.start();
    }
}