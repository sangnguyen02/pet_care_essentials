package com.example.uiux;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "AVvDAsHColpxPBeOrTMSEZhzA0vWFmZYv7aCYCDyJ_OvFRO8a-Vh84WlYAETFEKThIYqsI7DWA4y4hOf",
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                "com.example.uiux://paypalpay"
        ));
    }
}
