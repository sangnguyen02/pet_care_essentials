package com.example.uiux.Activities.User;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.User.Order.OrderPaymentActivity;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.CartAdapter;
import com.example.uiux.Model.CartItem;
import com.example.uiux.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private double totalAmount;

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
        updateTotalAmount();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Vuốt item nhưng không xóa ngay lập tức
                int position = viewHolder.getAdapterPosition();
                CartItem cartItem = cartItemList.get(position);

                // Hiển thị nút "Xóa" khi vuốt
                new AlertDialog.Builder(CartActivity.this)
                        .setTitle(getString(R.string.delete_cart_item_title))
                        .setMessage(getString(R.string.delete_cart_item_message))
                        .setPositiveButton(getString(R.string.confirm_delete), (dialog, which) -> {
                            cartItemList.remove(position);
                            cartAdapter.notifyItemRemoved(position);
                            // Chỉ cần gọi xóa từ Firebase
                            removeFromFirebase(cartItem);
                            // Xóa khỏi danh sách hiển thị trong adapter


                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                            // Hoàn tác vuốt, khôi phục item
                            cartAdapter.notifyItemChanged(position);
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                Drawable deleteIcon = ContextCompat.getDrawable(CartActivity.this, R.drawable.minus);
                ColorDrawable background = new ColorDrawable(ContextCompat.getColor(CartActivity.this, R.color.light_coral));

                if (dX < 0) { // Vuốt từ phải sang trái
                    background.setBounds(itemView.getRight() + (int) dX - backgroundCornerOffset, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    background.draw(c);

                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.7f;
            }

        };

        // Gán ItemTouchHelper vào RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rcv_cart);

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

        btn_buy.setOnClickListener(view -> {
            List<CartItem> selectedItems = cartAdapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Please select at least one item to proceed to payment.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra category của các item
            getCategoryForSupplies(selectedItems);
        });

    }

    void goToPayment() {


        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);

        startActivity(intent);
    }

    public void updateTotalAmount() {
        final double[] totalAmount = {0.0};
        List<CartItem> selectedItems = cartAdapter.getSelectedItems();
        final int[] remainingItemsToFetch = {selectedItems.size()}; // Đếm số lượng item còn lại để fetch

        if (remainingItemsToFetch[0] == 0) {
            // Nếu không có item nào được chọn, hiển thị 0
            tv_total_amount.setText(CurrencyFormatter.formatCurrency(totalAmount[0], getString(R.string.currency_vn)));
            return;
        }

        for (CartItem item : selectedItems) {
            // Lấy dữ liệu từ Firebase về
            DatabaseReference itemSelectedRef = FirebaseDatabase.getInstance().getReference("Cart")
                    .child(accountId).child(item.getCombinedKey());

            itemSelectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Lấy giá và số lượng từ Firebase
                    Double price = snapshot.child("supply_price").getValue(Double.class);
                    Integer quantity = snapshot.child("quantity").getValue(Integer.class);

                    if (price != null && quantity != null) {
                        totalAmount[0] += price * quantity; // Cộng dồn giá trị vào tổng
                    }

                    // Kiểm tra nếu đã lấy xong tất cả item
                    remainingItemsToFetch[0]--;
                    if (remainingItemsToFetch[0] == 0) {
                        tv_total_amount.setText(CurrencyFormatter.formatCurrency(totalAmount[0], getString(R.string.currency_vn))); // Cập nhật TextView với tổng số tiền
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                    Log.e("FirebaseError", "Error fetching data: " + error.getMessage());
                    remainingItemsToFetch[0]--; // Giảm số lượng items còn lại
                    if (remainingItemsToFetch[0] == 0) {
                        tv_total_amount.setText(CurrencyFormatter.formatCurrency(totalAmount[0], getString(R.string.currency_vn))); // Cập nhật với tổng số tiền hiện tại
                    }
                }
            });
        }
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
    public void getCategoryForSupplies(List<CartItem> selectedItems) {
        List<String> supplyIds = new ArrayList<>();
        for (CartItem item : selectedItems) {
            supplyIds.add(item.getSupply_id());
        }

        DatabaseReference supplyRef = FirebaseDatabase.getInstance().getReference("Supplies");
        List<String> categories = new ArrayList<>();

        for (String supplyId : supplyIds) {
            supplyRef.child(supplyId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Lấy thông tin category của sản phẩm
                    String category = snapshot.child("category").getValue(String.class);

                    if (category != null && !categories.contains(category)) {
                        categories.add(category);
                    }

                    // Kiểm tra xem đã có đủ dữ liệu từ tất cả các sản phẩm
                    if (categories.size() == supplyIds.size()) {
                        checkCategoryMatch(categories);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error fetching supply data: " + error.getMessage());
                }
            });
        }
    }

    private void checkCategoryMatch(List<String> categories) {
        if (categories.size() == 1) {
            // Tất cả các sản phẩm có cùng category
            String category = categories.get(0);
            // Thực hiện logic tiếp theo, ví dụ: putExtra category vào Intent
            goToPayment(category);
        } else {
            // Các sản phẩm không cùng category
            goToPayment("not value");
        }
    }

    private void goToPayment(String category) {
        List<CartItem> selectedItems = cartAdapter.getSelectedItems();
        ArrayList<String> selectedSupplies = new ArrayList<>();

        for (CartItem item : selectedItems) {
            selectedSupplies.add(item.getCombinedKey());
        }

        if (selectedSupplies.isEmpty()) {
            Toast.makeText(this, "Please select at least one item to proceed to payment.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putStringArrayListExtra("selected_supplies", selectedSupplies);
        intent.putExtra("categoryType", category);
        startActivity(intent);
    }


    private void removeFromFirebase(CartItem cartItem) {
        // Xóa item khỏi Firebase dựa trên accountId và khóa kết hợp (combinedKey)
        cartRef.child(cartItem.getCombinedKey()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Đã xóa thành công
                    updateTotalAmount();

                    Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi xóa
                    Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                });
    }


}