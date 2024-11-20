package com.example.uiux.Activities.User.Order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.R;
import com.example.uiux.ZaloPay.Api.CreateOrder;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPaymentActivity extends AppCompatActivity {
    TextView txtSoluong, txtTongTien;
    Button btnThanhToan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_payment);
        txtTongTien = findViewById(R.id.textViewTongTien);
        btnThanhToan = findViewById(R.id.buttonThanhToan);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        Intent intent = getIntent();
        Double total = intent.getDoubleExtra("totalPrice", (double) 0);
        String totalString = String.format("%.0f", total);
        Toast.makeText(getApplicationContext(),totalString , Toast.LENGTH_LONG).show();
        txtTongTien.setText(Double.toString(total));

        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(totalString);
                    //lblZpTransToken.setVisibility(View.VISIBLE);
                    String code = data.getString("returncode");
                    Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
                        String token = data.getString("zptranstoken");
                        ZaloPaySDK.getInstance().payOrder(OrderPaymentActivity.this, token, "demozpdk://app", new PayOrderListener()
                        {

                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                                intent1.putExtra("result","Thanh toan thanh cong");
                                startActivity(intent1);

                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                                intent1.putExtra("result","Thanh toan bi cancel");
                                startActivity(intent1);

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                                intent1.putExtra("result","Thanh toan bi loi");
                                startActivity(intent1);

                            }
                        });
                        //IsDone();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    }
