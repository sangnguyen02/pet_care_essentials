package com.example.uiux.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uiux.R;
import com.example.uiux.Utils.PasswordUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import Model.Account;

public class SignupTabFragment extends Fragment {
    View rootView;
    TextInputEditText edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    MaterialButton signUp;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.signup_tab_fragment, container, false);

        initWidget();
        signUp=rootView.findViewById(R.id.btn_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccount();
            }
        });

        return rootView;
    }


    void initWidget() {
        edtPhone = rootView.findViewById(R.id.edt_phone_number_sign_up);
        edtEmail = rootView.findViewById(R.id.edt_email);
        edtPassword = rootView.findViewById(R.id.edt_password_sign_up);
        edtConfirmPassword = rootView.findViewById(R.id.edt_confirm_password);
    }
    private void registerAccount() {
        String email = edtEmail.getText().toString().trim();
        String rawPassword  = edtPassword.getText().toString().trim();
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Kiểm tra điều kiện nhập liệu
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email không được để trống");
            return;
        }

        if (TextUtils.isEmpty(rawPassword )) {
            edtPassword.setError("Mật khẩu không được để trống");
            return;
        }

        if (!rawPassword .equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Sử dụng Firebase để đăng ký tài khoản với email và mật khẩu
        mAuth.createUserWithEmailAndPassword(email, hashedPassword)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công
                        Toast.makeText(getActivity(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                        // Gửi thông tin tài khoản đến cơ sở dữ liệu (nếu cần)
                        saveUserInfoToDatabase(phone, email,hashedPassword);
                    } else {
                        // Đăng ký thất bại
                        Toast.makeText(getActivity(), "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Phương thức lưu thông tin tài khoản vào Firebase Realtime Database
    private void saveUserInfoToDatabase(String phone, String email,String password) {
        // Giả sử bạn muốn lưu thông tin vào cơ sở dữ liệu
        String userId = mAuth.getCurrentUser().getUid();
        Account account=new Account();
        account.setEmail(email);
        account.setPhone(phone);
        account.setPassword(password);
        FirebaseDatabase.getInstance().getReference("Account").child(userId)
                .setValue(account)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Lưu thông tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
