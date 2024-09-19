package com.example.uiux.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uiux.R;
import com.example.uiux.Utils.EmailValidator;
import com.example.uiux.Utils.PasswordUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Model.Account;

public class SignupTabFragment extends Fragment {
    View rootView;
    TextInputEditText edtPhone, edtEmail;
    MaterialButton signUp;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.signup_tab_fragment, container, false);

        initWidget();
        signUp = rootView.findViewById(R.id.btn_sign_up);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Account");

        signUp.setOnClickListener(view -> registerAccount());

        return rootView;
    }

    void initWidget() {
        edtPhone = rootView.findViewById(R.id.edt_phone_number_sign_up);
        edtEmail = rootView.findViewById(R.id.edt_email);
    }

    private void registerAccount() {
        String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();

        // Kiểm tra điều kiện nhập liệu
        if (!EmailValidator.isValidEmail(edtEmail.getText().toString().trim())|| TextUtils.isEmpty(email)) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Số điện thoại không được để trống");
            return;
        }
        else if(!isValidPhoneNumber(edtPhone.getText().toString().trim()))
        {
            edtPhone.setError("Số điện thoại không hợp lệ");
            return;
        }

        // Kiểm tra xem email hoặc số điện thoại đã tồn tại hay chưa
        checkIfEmailOrPhoneExists(email, phone);
    }

    private void checkIfEmailOrPhoneExists(String email, String phone) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Email đã tồn tại
                    edtEmail.setError("Email đã tồn tại");
                    Toast.makeText(getActivity(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu email chưa tồn tại, kiểm tra số điện thoại
                    checkPhone(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPhone(String phone) {
        databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Số điện thoại đã tồn tại
                    edtPhone.setError("Số điện thoại đã tồn tại");
                    Toast.makeText(getActivity(), "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu cả email và số điện thoại chưa tồn tại, tiến hành đăng ký
                    createAccount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Có lỗi xảy ra: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount() {
        String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        String rawPassword = "123456";
        String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();



        // Sử dụng Firebase để đăng ký tài khoản với email và mật khẩu
        mAuth.createUserWithEmailAndPassword(email, rawPassword)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Gửi email xác thực
                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                if (verificationTask.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Email xác thực đã được gửi. Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
                                    saveUserInfoToDatabase(phone, email);
                                    mAuth.signOut(); // Đăng xuất sau khi đăng ký để đợi xác thực
                                } else {
                                    Toast.makeText(getActivity(), "Gửi email xác thực thất bại.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Đăng ký thất bại: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    // Phương thức lưu thông tin tài khoản vào Firebase Realtime Database
    private void saveUserInfoToDatabase(String phone, String email) {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Account account = new Account();
        account.setEmail(email);
        account.setPhone(phone);

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
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Số điện thoại phải bắt đầu với số 0 và có độ dài chính xác là 10 chữ số
        String regex = "^0\\d{9}$";

        // Kiểm tra xem số điện thoại có khớp với định dạng hay không
        return phoneNumber.matches(regex);
    }


}