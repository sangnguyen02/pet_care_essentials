package com.example.uiux.Activities.User.Profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Model.Account;

public class UserUpdateProfile extends AppCompatActivity {
    private Button btnBirthday, btnUpdate, btnChooseImage,btnAddress;
    private EditText tvGender, tvPhone, tvEmail, tvFullname;
    private TextView tvBirthday;
    private ImageView imgAvatar;
    private Uri imageUri;
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

        // Set up button listeners
        btnBirthday.setOnClickListener(view -> showDatePickerDialog());
        btnChooseImage.setOnClickListener(view -> chooseImage());
        btnUpdate.setOnClickListener(view -> updateAccountInfo());
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoAddress= new Intent(UserUpdateProfile.this, AddressActivity.class);
                gotoAddress.putExtra("account_id",account_id);
                startActivity(gotoAddress);
            }
        });
        loadUserProfile();
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

    private void updateAccountInfo() {
        String fullName = tvFullname.getText().toString().trim();
        String gender = tvGender.getText().toString().trim();
        String phone = tvPhone.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();

        String birthday = tvBirthday.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() ||  birthday.equals("Ngày sinh chưa chọn")) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the phone number exists in Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("Account");
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    account_id = userSnapshot.getKey(); // Get existing user ID
                    updateUserInFirebase(fullName, gender, phone, email, birthday);
                } else {
                    // If user does not exist, create a new profile
                    createUserInFirebase(fullName, gender, phone, email, birthday);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserUpdateProfile.this, "Failed to check user", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUserInFirebase(String fullName, String gender, String phone, String email,  String birthday) {
        DatabaseReference userRef = databaseReference.child(account_id);

        // Update individual fields directly in Firebase
        userRef.child("fullname").setValue(fullName);
        userRef.child("email").setValue(email);
        userRef.child("phone").setValue(phone);
        userRef.child("gender").setValue(gender);
        userRef.child("birthday").setValue(birthday);


        // Check if imageUri is not null, upload the image to Firebase Storage
        if (imageUri != null) {
            uploadImageToFirebase(imageUri, userRef); // Upload image and update its URL in the database
        } else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
    private void createUserInFirebase(String fullName, String gender, String phone, String email, String birthday) {
    DatabaseReference newUserRef = databaseReference.push();
    account_id = newUserRef.getKey(); // Get a new unique ID
    Model.Account account = new Model.Account();
    account.setFullname(fullName);
    account.setEmail(email);
    account.setPhone(phone);
    account.setGender(gender);
    account.setBirthday(birthday);

    account.setAccount_id(account_id);

    if (imageUri != null) {
        // Upload image and update account information after image upload
        uploadImageToFirebase(imageUri, newUserRef);
    } else {
        newUserRef.setValue(account).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "New profile created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create new profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

    private void uploadImageToFirebase(Uri imageUri, DatabaseReference userRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Account_Image/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Update the image URL in Firebase
                userRef.child("image").setValue(imageUrl).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserUpdateProfile.this, "Profile updated successfully with image", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserUpdateProfile.this, "Failed to update profile with image", Toast.LENGTH_SHORT).show();
                    }
                });
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
    private void loadUserProfile() {
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

        // Reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("Account");

        // Query Firebase to check if the user exists
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Model.Account account = snapshot.getValue(Model.Account.class);

                        // Populate the fields with the user's data
                        if (account != null) {
                            account_id = account.getAccount_id();
                            tvPhone.setText(account.getPhone()!=null?account.getPhone():" ");
                            tvFullname.setText(account.getFullname() != null ? account.getFullname() : "");
                            tvGender.setText(account.getGender() != null ? account.getGender() : "");
                            tvEmail.setText(account.getEmail() != null ? account.getEmail() : "");

                            tvBirthday.setText(account.getBirthday() != null ? account.getBirthday() : "");
                            // Load image if it exists
                            if (account.getImage() != null) {
                                Glide.with(UserUpdateProfile.this)
                                        .load(account.getImage())
                                        .into(imgAvatar);
                            }
                        }
                    }
                } else {
                    // If the user doesn't exist, prompt them to create a profile
                    Toast.makeText(UserUpdateProfile.this, "No existing profile found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(UserUpdateProfile.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
    private void  initWidget() {
        btnBirthday = findViewById(R.id.btn_birthday);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvFullname = findViewById(R.id.edt_fullname);
        tvGender = findViewById(R.id.edt_gender);
        tvPhone = findViewById(R.id.edt_phone);
        tvEmail = findViewById(R.id.edt_email);

        imgAvatar = findViewById(R.id.img_avatar);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnUpdate = findViewById(R.id.btn_update);
        btnAddress=findViewById(R.id.btn_addess);
    }
}
