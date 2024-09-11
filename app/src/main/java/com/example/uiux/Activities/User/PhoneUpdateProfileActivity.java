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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PhoneUpdateProfileActivity extends AppCompatActivity {
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_update_profile);
        initWidget();

        Intent intent = getIntent();
        String phoneNumber= intent.getStringExtra("PhoneNumer");
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            tvPhone.setText(phoneNumber);
        }

        btnBirthday.setOnClickListener(view -> showDatePickerDialog());
        btnChooseImage.setOnClickListener(view -> chooseImage());
        btnUpdate.setOnClickListener(view -> updateAccountInfo());
        loadUserProfile();


    }

//    private void updateAccountInfo() {
//        String fullName = tvFullname.getText().toString().trim();
//        String gender = tvGender.getText().toString().trim();
//        String phone = tvPhone.getText().toString().trim();
//        String email = tvEmail.getText().toString().trim();
//        String address = tvAddress.getText().toString().trim();
//        String birthday = tvBirthday.getText().toString().trim();
//
//        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() || birthday.equals("Ngày sinh chưa chọn")) {
//            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (imageUri != null) {
//            uploadImageToFirebase(imageUri);
//        } else {
//            saveAccountInfo(null);
//        }
//    }
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

    // Check if the phone number exists in Firebase database
    databaseReference = FirebaseDatabase.getInstance().getReference("Account");
    databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                account_id = userSnapshot.getKey(); // Get existing user ID
                updateUserInFirebase(fullName, gender, phone, email, address, birthday);
            } else {
                // If user does not exist, create a new profile
                createUserInFirebase(fullName, gender, phone, email, address, birthday);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to check user", Toast.LENGTH_SHORT).show();
        }
    });
}
    private void createUserInFirebase(String fullName, String gender, String phone, String email, String address, String birthday) {
        DatabaseReference newUserRef = databaseReference.push();
        account_id = newUserRef.getKey(); // Get a new unique ID
        Model.Account account = new Model.Account();
        account.setFullname(fullName);
        account.setEmail(email);
        account.setPhone(phone);
        account.setGender(gender);
        account.setBirthday(birthday);
        account.setAddress(address);
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

//    private void updateUserInFirebase(String fullName, String gender, String phone, String email, String address, String birthday) {
//        DatabaseReference userRef = databaseReference.child(account_id);
//        Model.Account account = new Model.Account();
//        account.setFullname(fullName);
//        account.setEmail(email);
//        account.setPhone(phone);
//        account.setGender(gender);
//        account.setBirthday(birthday);
//        account.setAddress(address);
//        account.setAccount_id(account_id);
//
//        if (imageUri != null) {
//            uploadImageToFirebase(imageUri); // Upload image to Firebase if available
//        } else {
//            userRef.setValue(account).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
private void updateUserInFirebase(String fullName, String gender, String phone, String email, String address, String birthday) {
    DatabaseReference userRef = databaseReference.child(account_id);

    // Update individual fields directly in Firebase
    userRef.child("fullname").setValue(fullName);
    userRef.child("email").setValue(email);
    userRef.child("phone").setValue(phone);
    userRef.child("gender").setValue(gender);
    userRef.child("birthday").setValue(birthday);
    userRef.child("address").setValue(address);

    // Check if imageUri is not null, upload the image to Firebase Storage
    if (imageUri != null) {
        uploadImageToFirebase(imageUri, userRef); // Upload image and update its URL in the database
    } else {
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
    //    private void uploadImageToFirebase(Uri imageUri) {
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        StorageReference imageRef = storageRef.child("Account_Image/" + System.currentTimeMillis() + ".jpg");
//
//        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
//            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String imageUrl = uri.toString();
//                saveAccountInfo(imageUrl);
//            }).addOnFailureListener(e -> {
//                Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
//            });
//        }).addOnFailureListener(e -> {
//            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
//        });
//    }
//private void uploadImageToFirebase(Uri imageUri) {
//    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//    StorageReference imageRef = storageRef.child("Account_Image/" + System.currentTimeMillis() + ".jpg");
//
//    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
//        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            String imageUrl = uri.toString();
//            if (account_id != null) {
//                // If updating an existing user
//                databaseReference.child(account_id).child("image").setValue(imageUrl).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(PhoneUpdateProfileActivity.this, "Image uploaded and profile updated", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                // If creating a new user
//                createUserInFirebaseWithImage(imageUrl);
//            }
//        });
//    }).addOnFailureListener(e -> {
//        Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
//    });
//}
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
    private void createUserInFirebaseWithImage(String imageUrl) {
        DatabaseReference newUserRef = databaseReference.push();
        account_id = newUserRef.getKey(); // Get a new unique ID
        Model.Account account = new Model.Account();
        account.setFullname(tvFullname.getText().toString().trim());
        account.setEmail(tvEmail.getText().toString().trim());
        account.setPhone(tvPhone.getText().toString().trim());
        account.setGender(tvGender.getText().toString().trim());
        account.setBirthday(tvBirthday.getText().toString().trim());
        account.setAddress(tvAddress.getText().toString().trim());
        account.setImage(imageUrl); // Set the image URL
        account.setAccount_id(account_id);

        newUserRef.setValue(account).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "New profile created successfully with image", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
//    private void loadUserProfile() {
//        String phone = tvPhone.getText().toString().trim();
//
//        // Reference to the Firebase database
//        databaseReference = FirebaseDatabase.getInstance().getReference("Account");
//
//        // Query Firebase to check if the user exists
//        databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // Get user data
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Model.Account account = snapshot.getValue(Model.Account.class);
//
//                        // Populate the fields with the user's data
//                        if (account != null) {
//                            account_id = account.getAccount_id();
//                            if(account.getFullname()!=null)
//                            {
//                                tvFullname.setText(account.getFullname());
//                            }
//                            if(account.getGender()!=null)
//                            {
//                                tvGender.setText(account.getGender());
//                            }
//                            if(account.getEmail()!=null)
//                            {
//                                tvEmail.setText(account.getEmail());
//                            }
//                            if(account.getAddress()!=null)
//                            {
//                                tvAddress.setText(account.getAddress());
//                            }
//                            if(account.getBirthday()!=null)
//                            {
//                                tvBirthday.setText(account.getBirthday());
//                            }
//
//                            // Load image if it exists
//                            if (account.getImage() != null) {
//                                // Use an image loading library like Picasso or Glide
//                                Glide.with(PhoneUpdateProfileActivity.this)
//                                        .load(account.getImage())
//                                        .into(imgAvatar);
//                            }
//                        }
//                    }
//                } else {
//                    // If the user doesn't exist, prompt them to create a profile
//                    Toast.makeText(PhoneUpdateProfileActivity.this, "No existing profile found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle errors
//                Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
private void loadUserProfile() {
    String phone = tvPhone.getText().toString().trim();

    if (phone.isEmpty()) {
        Toast.makeText(this, "Phone number is empty", Toast.LENGTH_SHORT).show();
        return;
    }

    // Reference to the Firebase database
    databaseReference = FirebaseDatabase.getInstance().getReference("Account");

    // Query Firebase to check if the user exists
    databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                // Get user data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model.Account account = snapshot.getValue(Model.Account.class);

                    // Populate the fields with the user's data
                    if (account != null) {
                        account_id = account.getAccount_id();
                        tvFullname.setText(account.getFullname() != null ? account.getFullname() : "");
                        tvGender.setText(account.getGender() != null ? account.getGender() : "");
                        tvEmail.setText(account.getEmail() != null ? account.getEmail() : "");
                        tvAddress.setText(account.getAddress() != null ? account.getAddress() : "");
                        tvBirthday.setText(account.getBirthday() != null ? account.getBirthday() : "");

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
        public void onCancelled(DatabaseError databaseError) {
            // Handle errors
            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}



    private void initWidget()
    {
        btnBirthday = findViewById(R.id.btn_birthday2);
        tvBirthday = findViewById(R.id.tv_birthday2);
        tvFullname = findViewById(R.id.edt_fullname2);
        tvGender = findViewById(R.id.edt_gender2);
        tvPhone = findViewById(R.id.edt_phone2);
        tvEmail = findViewById(R.id.edt_email2);
        tvAddress = findViewById(R.id.edt_address2);
        imgAvatar = findViewById(R.id.img_avatar2);
        btnChooseImage = findViewById(R.id.btn_choose_image2);
        btnUpdate = findViewById(R.id.btn_update2);
    }
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
//    private void saveAccountInfo(String imageUrl) {
//        databaseReference = FirebaseDatabase.getInstance().getReference("Account");
//        //phoneNum=  countryCodePicker.getFullNumberWithPlus();
//        DatabaseReference newUserRef = databaseReference.push();
//        String phone = tvPhone.getText().toString().trim();
//        account_id= newUserRef.getKey();
//        Log.e("Key ID",account_id);
//        Model.Account account = new Model.Account();
//        account.setFullname(tvFullname.getText().toString().trim());
//        account.setEmail(tvEmail.getText().toString().trim());
//        account.setPhone(tvPhone.getText().toString().trim());
//        account.setGender(tvGender.getText().toString().trim());
//        account.setBirthday(tvBirthday.getText().toString().trim());
//        account.setAddress(tvAddress.getText().toString().trim());
//        account.setAccount_id(account_id);
//        if (imageUrl != null) {
//            account.setImage(imageUrl);
//        }
//
//        if (account_id != null) {
//            databaseReference.child(account_id).setValue(account).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(PhoneUpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
private void saveAccountInfo(String imageUrl) {
    databaseReference = FirebaseDatabase.getInstance().getReference("Account");
    String phone = tvPhone.getText().toString().trim();

    // Kiểm tra xem số điện thoại đã tồn tại chưa
    databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                // Nếu số điện thoại đã tồn tại
                Toast.makeText(PhoneUpdateProfileActivity.this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();

            } else {
                // Nếu số điện thoại chưa tồn tại, thêm mới
                DatabaseReference newUserRef = databaseReference.push();
                account_id = newUserRef.getKey();
                Log.e("Key ID", account_id);

                Model.Account account = new Model.Account();
                account.setFullname(tvFullname.getText().toString().trim());
                account.setEmail(tvEmail.getText().toString().trim());
                account.setPhone(phone);
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
                            Toast.makeText(PhoneUpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PhoneUpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Xử lý lỗi nếu cần
        }
    });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri); // Set the selected image to the ImageView
        }
    }


}