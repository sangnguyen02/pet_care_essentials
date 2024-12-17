package com.example.uiux.Activities.User.Profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhoneUpdateProfileActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private TextInputEditText edt_fullname, edt_gender, edt_phone, edt_email, edt_birthday;
    private CircleImageView imgAvatar;
    private ImageView img_back, img_update;
    private Uri imageUri;
    private String account_id;
    private DatabaseReference databaseReference;
    private final Calendar calendar = Calendar.getInstance();
    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.light_bg));
        setContentView(R.layout.activity_phone_update_profile);
        initWidget();

        Intent intent = getIntent();
        String phoneNumber= intent.getStringExtra("phone_no");
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            edt_phone.setText(phoneNumber);
        }

//        btnAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent gotoAddress= new Intent(PhoneUpdateProfileActivity.this, AddressActivity.class);
//                gotoAddress.putExtra("account_id",account_id);
//                startActivity(gotoAddress);
//            }
//        });

        imgAvatar.setOnClickListener(view -> chooseImage());
        img_update.setOnClickListener(view -> updateAccountInfo());
        img_back.setOnClickListener(view -> back());
        loadUserProfile();


    }



    private void updateAccountInfo() {
    String fullName = Objects.requireNonNull(edt_fullname.getText()).toString().trim();
    String gender = Objects.requireNonNull(edt_gender.getText()).toString().trim();
    String phone = Objects.requireNonNull(edt_phone.getText()).toString().trim();
    String email = Objects.requireNonNull(edt_email.getText()).toString().trim();
    String birthday = Objects.requireNonNull(edt_birthday.getText()).toString().trim();

    if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() ||  birthday.equals("Ngày sinh chưa chọn")) {
        Toast.makeText(this, "Please enter complete information", Toast.LENGTH_SHORT).show();
        return;
    }

    // Check if the phone number exists in Firebase database
    databaseReference = FirebaseDatabase.getInstance().getReference("Account");
    databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to check user", Toast.LENGTH_SHORT).show();
        }
    });
}

private void back() {
        finish();
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
           // uploadImageToFirebase(imageUri); // Upload image to Firebase if available
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

private void updateUserInFirebase(String fullName, String gender, String phone, String email, String birthday) {
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
    private void uploadImageToFirebase(Uri imageUri, DatabaseReference userRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Account_Image/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Update the image URL in Firebase
                userRef.child("image").setValue(imageUrl).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PhoneUpdateProfileActivity.this, "Profile updated successfully with image", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to update profile with image", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }
private void loadUserProfile() {
    String phone = Objects.requireNonNull(edt_phone.getText()).toString().trim();

    if (phone.isEmpty()) {
        Toast.makeText(this, "Phone number is empty", Toast.LENGTH_SHORT).show();
        return;
    }

    // Reference to the Firebase database
    databaseReference = FirebaseDatabase.getInstance().getReference("Account");

    // Query Firebase to check if the user exists
    databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                // Get user data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model.Account account = snapshot.getValue(Model.Account.class);

                    // Populate the fields with the user's data
                    if (account != null) {
                        account_id = account.getAccount_id();
                        edt_fullname.setText(account.getFullname() != null ? account.getFullname() : "");
                        edt_gender.setText(account.getGender() != null ? account.getGender() : "");
                        edt_email.setText(account.getEmail() != null ? account.getEmail() : "");
                        edt_birthday.setText(account.getBirthday() != null ? account.getBirthday() : "");

                        // Load image if it exists
                        if (account.getImage() != null) {
                            Glide.with(PhoneUpdateProfileActivity.this)
                                    .load(account.getImage())
                                    .into(imgAvatar);
                        }
                    }
                }
            } else {
                // If the user doesn't exist, prompt them to create a profile
                Toast.makeText(PhoneUpdateProfileActivity.this, "No existing profile found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle errors
            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

    private void initWidget()
    {
        edt_fullname = findViewById(R.id.edt_fullname2);
        edt_gender = findViewById(R.id.edt_gender2);
        edt_phone = findViewById(R.id.edt_phone2);
        edt_email = findViewById(R.id.edt_email2);
        edt_birthday = findViewById(R.id.edt_birthday2);
        imgAvatar = findViewById(R.id.img_avatar2);
        img_back = findViewById(R.id.img_back);
        img_update = findViewById(R.id.img_update);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        }
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





}