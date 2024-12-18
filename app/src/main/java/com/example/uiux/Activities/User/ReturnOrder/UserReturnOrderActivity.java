package com.example.uiux.Activities.User.ReturnOrder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.InfoReturnOrder;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class UserReturnOrderActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView img1, img2, img3, img4;
    private Uri[] imageUris = new Uri[4];
    private FirebaseStorage storage;
    private DatabaseReference returnOrderRef;
    private  DatabaseReference orderRef;
    private  DatabaseReference accountRef;
    private RadioGroup rgReturnReasons;
    private EditText etOtherReason;
    private MaterialButton btnReturnOrder;
    private ImageView img_back_return_order;
    private String orderId;
    private  String accountId;
    private  String name,email,phone,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_user_return_order);
        initWidget();
        img1.setOnClickListener(view -> openImageChooser(0));
        img2.setOnClickListener(view -> openImageChooser(1));
        img3.setOnClickListener(view -> openImageChooser(2));
        img4.setOnClickListener(view -> openImageChooser(3));

        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        accountRef = FirebaseDatabase.getInstance().getReference("Account");
        returnOrderRef = FirebaseDatabase.getInstance().getReference("Return Order");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("order_id");
        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);

        // Xử lý sự kiện chọn lý do
        rgReturnReasons.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_other_reason) {
                // Hiển thị EditText nếu chọn "Lý do khác"
                etOtherReason.setVisibility(View.VISIBLE);
            } else {
                // Ẩn EditText nếu chọn lý do khác
                etOtherReason.setVisibility(View.GONE);
            }
        });
        btnReturnOrder.setOnClickListener(v -> returnOrder());

    }

    private void returnOrder() {
        getAccountInfo(); // Lấy thông tin tài khoản

        // Xác định lý do trả hàng
        int selectedReasonId = rgReturnReasons.getCheckedRadioButtonId();
        int reason = 0; // Default reason code
        String detailReason = "";

        // Kiểm tra lý do chọn
        if (selectedReasonId == R.id.rb_reason1) {
            reason = 1; // "Tôi đặt nhầm sản phẩm"
        } else if (selectedReasonId == R.id.rb_reason2) {
            reason = 2; // "Thời gian giao hàng quá lâu"
        } else if (selectedReasonId == R.id.rb_reason3) {
            reason = 3; // "Sản phẩm đã tìm thấy giá tốt hơn"
        } else if (selectedReasonId == R.id.rb_other_reason) {
            reason = 4; // "Lý do khác"
            detailReason = etOtherReason.getText().toString().trim(); // Lấy lý do nhập tay
        }

        // Kiểm tra lý do hợp lệ
        if (reason == 4 && detailReason.isEmpty()) {
            Toast.makeText(this, "Please enter another reason!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tải ảnh lên Firebase Storage
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < imageUris.length; i++) {
            if (imageUris[i] != null) {
                uploadImageToFirebase(imageUris[i], i, imageUrls, reason, detailReason);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri, int index, List<String> imageUrls, int reason, String detailReason) {
        String fileName = "return_images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrls.add(uri.toString()); // Lưu URL ảnh

                    // Nếu là ảnh cuối cùng, tạo yêu cầu trả hàng
                    if (imageUrls.size() == imageUris.length) {
                        createReturnOrder(reason, detailReason, imageUrls);
                    }
                })
        ).addOnFailureListener(e -> Log.e("UploadImage", "Failed: " + e.getMessage()));
    }

    private void createReturnOrder(int reason, String detailReason, List<String> imageUrls) {
        String returnOrderId = returnOrderRef.push().getKey();
        if (returnOrderId == null) {
            Log.e("ReturnOrder", "Failed to generate unique ID");
            return;
        }

        // Tạo đối tượng InfoReturnOrder
        InfoReturnOrder returnOrder = new InfoReturnOrder();
        returnOrder.setInfo_return_id(returnOrderId);
        returnOrder.setOrder_id(orderId);
        returnOrder.setReason(reason);
        returnOrder.setDetail_reason(detailReason);
        returnOrder.setRequest_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        returnOrder.setName(name);
        returnOrder.setAddress(address);
        returnOrder.setPhone_number(phone);
        returnOrder.setEmail(email);
        returnOrder.setImageUrls(imageUrls);

        // Lưu vào Firebase
        returnOrderRef.child(returnOrderId).setValue(returnOrder).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateOrderStatus(6);
                Toast.makeText(this, "Return request has been sent!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e("ReturnOrder", "Failed to save return order: " + task.getException());
            }
        });
    }

    private void getAccountInfo() {
        if (accountId == null || accountId.isEmpty()) {
            Log.e("AccountInfo", "Account ID is null or empty.");
            return;
        }

        accountRef.child(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Map data from snapshot to Account model
                    Model.Account account = snapshot.getValue(Model.Account.class);
                    if (account != null) {
                        // Extract required fields for return order
                         name = account.getFullname();
                         email = account.getEmail();
                         phone = account.getPhone();
                         address = account.getAddress();

                        // Log the extracted information for debugging
                        Log.d("AccountInfo", "Name: " + name);
                        Log.d("AccountInfo", "Email: " + email);
                        Log.d("AccountInfo", "Phone: " + phone);
                        Log.d("AccountInfo", "Address: " + address);

                        // Use this information as needed for the return order
                        // For example, store in instance variables or UI fields
                    }
                } else {
                    Log.e("AccountInfo", "Account not found for ID: " + accountId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountInfo", "Database error: " + error.getMessage());
            }
        });
    }
    private void updateOrderStatus(int newStatus) {
        if (orderId == null || orderId.isEmpty()) {
            Log.e("UpdateOrderStatus", "Order ID is null or empty.");
            return;
        }

        // Cập nhật trường "status" trong đơn hàng
        orderRef.child(orderId).child("status").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


            } else {


            }
        });
    }


    private void initWidget() {
        img_back_return_order = findViewById(R.id.img_back_return_order);
        img_back_return_order.setOnClickListener(view -> finish());
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        rgReturnReasons = findViewById(R.id.rg_return_reasons);
        etOtherReason = findViewById(R.id.et_other_reason);
        btnReturnOrder = findViewById(R.id.btn_return_order);
    }
    private void openImageChooser(int index) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST + index);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            int imageIndex = requestCode - PICK_IMAGE_REQUEST; // Adjust index based on requestCode
            if (imageIndex >= 0 && imageIndex < imageUris.length) { // Ensure index is within bounds
                imageUris[imageIndex] = selectedImageUri; // Store selected URI in the array
                switch (imageIndex) {
                    case 0:
                        img1.setImageURI(selectedImageUri);
                        break;
                    case 1:
                        img2.setImageURI(selectedImageUri);
                        break;
                    case 2:
                        img3.setImageURI(selectedImageUri);
                        break;
                    case 3:
                        img4.setImageURI(selectedImageUri);
                        break;
                }
            }
        }
    }
}