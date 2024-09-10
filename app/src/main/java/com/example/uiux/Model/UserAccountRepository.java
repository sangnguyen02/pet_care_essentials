package com.example.uiux.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

public class UserAccountRepository {

    private DatabaseReference databaseReference;

    public UserAccountRepository() {
        // Khởi tạo Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Account");
    }

    // Hàm lấy thông tin tài khoản từ số điện thoại hoặc tạo mới nếu không tồn tại
    public void getAccountByPhone(String phone) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                String accountId = null;

                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    Model.Account account = accountSnapshot.getValue(Model.Account.class);
                    if (account != null && account.getPhone().equals(phone)) {
                        // Tìm thấy tài khoản
                        found = true;
                        accountId = account.getAccount_id();
                        Log.d("Account Info", "ID: " + account.getAccount_id());
                        Log.d("Account Info", "Full Name: " + account.getFullname());
                        Log.d("Account Info", "Email: " + account.getEmail());
                        Log.d("Account Info", "Phone: " + account.getPhone());
                        break;
                    }
                }

                if (!found) {
                    // Tạo tài khoản mới với thông tin trống
                    String newAccountId = databaseReference.push().getKey();  // Tạo ID ngẫu nhiên cho tài khoản mới
                    Model.Account newAccount = new Model.Account();
                    newAccount.setAccount_id(newAccountId);
                    newAccount.setPhone(phone);  // Chỉ thiết lập số điện thoại, các thông tin khác để trống

                    // Lưu tài khoản mới vào Firebase Realtime Database
                    databaseReference.child(newAccountId).setValue(newAccount)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Tài khoản mới đã được tạo với số điện thoại: " + phone);
                                } else {
                                    Log.e("Firebase", "Lỗi khi tạo tài khoản mới: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });
    }
}
