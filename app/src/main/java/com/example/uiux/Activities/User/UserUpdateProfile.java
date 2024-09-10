package com.example.uiux.Activities.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class UserUpdateProfile extends AppCompatActivity {
    private Button btnBirthday, btnUpdate, btnChooseImage;
    private TextView tvBirthday, tvGender, tvPhone, tvEmail, tvAddress, tvFullname;
    private ImageView imgAvatar;
    private Uri imageUri;

    private DatabaseReference accountRef;
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        // Khởi tạo các widget
        initWidget();

        // Lấy thông tin người dùng hiện tại từ Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        accountRef = FirebaseDatabase.getInstance().getReference("Account").child(currentUser.getUid());
        storageRef = FirebaseStorage.getInstance().getReference("Account_Image");

        // Hiển thị thông tin người dùng
        loadUserInfo();

        // Chọn ngày sinh bằng DatePickerDialog
        btnBirthday.setOnClickListener(view -> showDatePickerDialog());

        // Chọn ảnh đại diện
        btnChooseImage.setOnClickListener(view -> chooseImage());

        // Cập nhật thông tin
        btnUpdate.setOnClickListener(view -> updateAccountInfo());
    }

    // Tải thông tin người dùng từ Firebase và hiển thị lên giao diện
    private void loadUserInfo() {
        accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Kiểm tra và cập nhật từng trường
                    String fullName = snapshot.child("fullname").getValue(String.class);
                    if (fullName != null && !fullName.isEmpty()) {
                        tvFullname.setText(fullName);
                    }

                    String gender = snapshot.child("gender").getValue(String.class);
                    if (gender != null && !gender.isEmpty()) {
                        tvGender.setText(gender);
                    }

                    String phone = snapshot.child("phone").getValue(String.class);
                    if (phone != null && !phone.isEmpty()) {
                        tvPhone.setText(phone);
                    }

                    String email = snapshot.child("email").getValue(String.class);
                    if (email != null && !email.isEmpty()) {
                        tvEmail.setText(email);
                    }

                    String address = snapshot.child("address").getValue(String.class);
                    if (address != null && !address.isEmpty()) {
                        tvAddress.setText(address);
                    }

                    String birthday = snapshot.child("birthday").getValue(String.class);
                    if (birthday != null && !birthday.isEmpty()) {
                        tvBirthday.setText(birthday);
                    }

                    String avatarUrl = snapshot.child("avatar").getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(UserUpdateProfile.this).load(avatarUrl).into(imgAvatar);
                    }
                } else {
                    Toast.makeText(UserUpdateProfile.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserUpdateProfile.this, "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Mở hộp thoại chọn ngày sinh
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvBirthday.setText(sdf.format(calendar.getTime()));  // Hiển thị ngày sinh đã chọn
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Hàm chọn ảnh từ thư viện
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    // Cập nhật thông tin tài khoản
    private void updateAccountInfo() {
        String fullName = tvFullname.getText().toString().trim();
        String gender = tvGender.getText().toString().trim();
        String phone = tvPhone.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();
        String address = tvAddress.getText().toString().trim();
        String birthday = tvBirthday.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() || birthday.equals("Ngày sinh chưa chọn")) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("fullname", fullName);
        updateMap.put("gender", gender);
        updateMap.put("phone", phone);
        updateMap.put("email", email);
        updateMap.put("address", address);
        updateMap.put("birthday", birthday);

        // Kiểm tra nếu có ảnh đại diện, cập nhật lên Firebase Storage
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(currentUser.getUid() + ".jpg");
            fileRef.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateMap.put("avatar", uri.toString());
                        accountRef.updateChildren(updateMap).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(UserUpdateProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserUpdateProfile.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu không có hình ảnh, chỉ cập nhật thông tin khác
            accountRef.updateChildren(updateMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(UserUpdateProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserUpdateProfile.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Nhận kết quả khi người dùng chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);  // Hiển thị ảnh đã chọn
        }
    }

    // Khởi tạo các widget
    private void initWidget() {
        btnBirthday = findViewById(R.id.btn_birthday);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvFullname = findViewById(R.id.edt_fullname);
        tvGender = findViewById(R.id.edt_gender);
        tvPhone = findViewById(R.id.edt_phone);
        tvEmail = findViewById(R.id.edt_email);
        tvAddress = findViewById(R.id.edt_address);
        imgAvatar = findViewById(R.id.img_avatar);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnUpdate = findViewById(R.id.btn_update);
    }
}
