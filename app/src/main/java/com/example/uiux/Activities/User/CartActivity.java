package com.example.uiux.Activities.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.CartAdapter;
import com.example.uiux.Model.CartItem;
import com.example.uiux.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    TextView tv_total_amount;
    MaterialButton btn_buy;
    MaterialCheckBox checkBox_all;
    ImageView img_back_my_cart;
    RecyclerView rcv_cart;
    CartAdapter cartAdapter;
    List<CartItem> cartItemList = new ArrayList<>();
    List<CartItem> selectedItems = new ArrayList<>();
    DatabaseReference cartRef;
    String accountId;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_cart);

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        initWidget();
        loadCartItems();

    }

    private void initWidget() {
        img_back_my_cart = findViewById(R.id.img_back_my_cart);
        img_back_my_cart.setOnClickListener(view -> finish());
        rcv_cart = findViewById(R.id.rcv_cart);
        rcv_cart.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartAdapter = new CartAdapter(this, cartItemList);
        rcv_cart.setAdapter(cartAdapter);
        tv_total_amount = findViewById(R.id.tv_total_amount_price);
        btn_buy = findViewById(R.id.btn_buy);
        checkBox_all = findViewById(R.id.checkbox_all_item);

        checkBox_all.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartAdapter.selectAllItems(isChecked);
            updateTotalAmount();
        });
    }

    public void updateTotalAmount() {
        double totalAmount = 0.0;
        for (CartItem item : cartAdapter.getSelectedItems()) {
            totalAmount += item.getTotalPrice();
        }
        tv_total_amount.setText(String.valueOf(totalAmount));
    }

    private void loadCartItems() {
        if(!accountId.isEmpty()) {
            cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(accountId);
            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cartItemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem cartItem = snapshot.getValue(CartItem.class);
                        cartItemList.add(cartItem);
                    }
                    cartAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }


}