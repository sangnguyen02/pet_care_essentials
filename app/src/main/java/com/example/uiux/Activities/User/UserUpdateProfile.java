package com.example.uiux.Activities.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Model.Account;

public class UserUpdateProfile extends AppCompatActivity {
    private Button btnBirthday, btnUpdate, btnChooseImage;
    private EditText tvGender, tvPhone, tvEmail, tvAddress, tvFullname;
    private TextView tvBirthday;
    private ImageView imgAvatar;
    private Uri imageUri;
    private CountryCodePicker countryCodePicker;
    private String account_id;
    private DatabaseReference databaseReference;

    private Calendar calendar = Calendar.getInstance();
    private static final int PICK_IMAGE_REQUEST = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        // Initialize widgets
        initWidget();

        // Get data from Intent
        Intent intent = getIntent();
        String fullname = intent.getStringExtra("fullname");
        String email = intent.getStringExtra("email");
        String imageUrl = intent.getStringExtra("image");
        account_id = intent.getStringExtra("account_id");

        // Display user information
        if (fullname != null && !fullname.isEmpty()) {
            tvFullname.setText(fullname);
        }
        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imgAvatar);
        }

        // Set up button listeners
        btnBirthday.setOnClickListener(view -> showDatePickerDialog());
        btnChooseImage.setOnClickListener(view -> chooseImage());
        btnUpdate.setOnClickListener(view -> updateAccountInfo());
    }

    // Show date picker dialog
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvBirthday.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Choose image from gallery
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Update account information
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

        if (imageUri != null) {
            uploadImageToFirebase(imageUri);
        } else {
            saveAccountInfo(null);
        }
    }

    // Upload image to Firebase Storage
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Account_Image/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveAccountInfo(imageUrl);
            }).addOnFailureListener(e -> {
                Toast.makeText(UserUpdateProfile.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(UserUpdateProfile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    // Save account information to Firebase Realtime Database
    private void saveAccountInfo(String imageUrl) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Account");
        //phoneNum=  countryCodePicker.getFullNumberWithPlus();

        Account account = new Account();
        account.setFullname(tvFullname.getText().toString().trim());
        account.setEmail(tvEmail.getText().toString().trim());
        account.setPhone(tvPhone.getText().toString().trim());
        account.setGender(tvGender.getText().toString().trim());
        account.setBirthday(tvBirthday.getText().toString().trim());
        account.setAddress(tvAddress.getText().toString().trim());
        account.setAccount_id(account_id);
        if (imageUrl != null) {
            account.setImage(imageUrl);
        }

        if (account_id != null) {
            databaseReference.child(account_id).setValue(account).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(UserUpdateProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserUpdateProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        }
    }

    // Initialize widgets
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
