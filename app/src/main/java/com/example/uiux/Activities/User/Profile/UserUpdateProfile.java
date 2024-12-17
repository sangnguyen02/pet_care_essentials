package com.example.uiux.Activities.User.Profile;

import android.app.AlertDialog;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private MaterialButton btnUpdate;
    private TextInputEditText edt_fullname, edt_gender, edt_phone, edt_email, edt_birthday;
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

        imgAvatar.setOnClickListener(view -> chooseImage());
        btnUpdate.setOnClickListener(view -> updateAccountInfo());

        loadUserProfile();
    }

    private void showGenderDialog() {
        String[] genders = getResources().getStringArray(R.array.genders);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose gender")
                .setItems(genders, (dialog, which) -> {
                    edt_gender.setText(genders[which]);
                });
        builder.create().show();
    }

    // Show date picker dialog
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            edt_birthday.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    // Choose image from gallery
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void updateAccountInfo() {
        String fullName = edt_fullname.getText().toString().trim();
        String gender = edt_gender.getText().toString().trim();
        String phone = edt_phone.getText().toString().trim();
        String email = edt_email.getText().toString().trim();

        String birthday = edt_birthday.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() ||  birthday.isEmpty()) {
            Toast.makeText(this, "Please enter complete information", Toast.LENGTH_SHORT).show();
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
        account.setFullname(edt_fullname.getText().toString().trim());
        account.setEmail(edt_email.getText().toString().trim());
        account.setPhone(edt_phone.getText().toString().trim());
        account.setGender(edt_gender.getText().toString().trim());
        account.setBirthday(edt_birthday.getText().toString().trim());

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
            edt_fullname.setText(fullname);
        }
        if (email != null && !email.isEmpty()) {
            edt_email.setText(email);
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
                            edt_phone.setText(account.getPhone()!=null?account.getPhone():" ");
                            edt_fullname.setText(account.getFullname() != null ? account.getFullname() : "");
                            edt_gender.setText(account.getGender() != null ? account.getGender() : "");
                            edt_email.setText(account.getEmail() != null ? account.getEmail() : "");

                            edt_birthday.setText(account.getBirthday() != null ? account.getBirthday() : "");
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
        edt_fullname = findViewById(R.id.edt_fullname);
        edt_gender = findViewById(R.id.edt_gender);
        edt_phone = findViewById(R.id.edt_phone);
        edt_email = findViewById(R.id.edt_email);
        edt_birthday = findViewById(R.id.edt_birthday);
        imgAvatar = findViewById(R.id.img_avatar);
        btnUpdate = findViewById(R.id.btn_update);

        // Chọn giới tính
        edt_gender.setOnClickListener(v -> showGenderDialog());

        // Chọn ngày sinh
        edt_birthday.setOnClickListener(v -> showDatePickerDialog());

        // Khởi tạo ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                imageUri = result.getData().getData();
                imgAvatar.setImageURI(imageUri);
            }
        });
    }
}
