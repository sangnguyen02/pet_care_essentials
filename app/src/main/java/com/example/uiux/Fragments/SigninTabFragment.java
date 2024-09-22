package com.example.uiux.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uiux.Activities.Sent_OTPActivity;
import com.example.uiux.R;
import com.example.uiux.Utils.PhoneNumberValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SigninTabFragment extends Fragment {
    View rootView;
    TextInputEditText edtPhone;
    private FirebaseAuth mAuth;
    String phoneNumber;
    MaterialButton signIn;
    CountryCodePicker countryCodePicker;
    Long timeoutSeconds = 30L;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    private static final String TAG = SigninTabFragment.class.getName();
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.signin_tab_fragment, container, false);
        initWidget();
        mAuth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth here

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtPhone.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Phone", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = countryCodePicker.getFullNumberWithPlus();
                Log.e("Phone Number", phoneNumber);
                String phone = edtPhone.getText().toString().trim();
                if (!countryCodePicker.isValidFullNumber() || !PhoneNumberValidator.isValidPhoneNumber(phone)) {
                    edtPhone.setError("Phone number not valid");

                } else {
                    if (mAuth == null) {
                        Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SentOTP(phoneNumber);
                }
            }
        });

        return rootView;
    }

    void initWidget() {
        progressBar = rootView.findViewById(R.id.progressBar);
        countryCodePicker = rootView.findViewById(R.id.login_country_code2);
        edtPhone = rootView.findViewById(R.id.edt_phone_number);
        signIn = rootView.findViewById(R.id.btn_sign_in);
        countryCodePicker.registerCarrierNumberEditText(edtPhone);

        // Thêm sự kiện cho CountryCodePicker để tùy chỉnh kích thước dialog
        countryCodePicker.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialogInterface) {

                // Chỉnh kích thước của dialog (ví dụ: rộng 800px và cao 600px)
                ((Dialog) dialogInterface).getWindow().setLayout(800, 800);
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
                // Xử lý khi dialog đóng
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
                // Xử lý khi dialog bị hủy
            }
        });
    }

    private void SentOTP(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        signIn.setVisibility(View.INVISIBLE);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)            // Phone number to verify
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS) // Timeout duration
                .setActivity(getActivity())             // Activity (for callback binding)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
                        progressBar.setVisibility(View.GONE);
                        signIn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                        Toast.makeText(getActivity(), "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        signIn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Log.d(TAG, "onCodeSent: " + verificationId);
                        resendingToken = token;
                        String fullPhoneNumber = countryCodePicker.getFullNumberWithPlus();

                        Intent intent = new Intent(getContext(), Sent_OTPActivity.class);
                        intent.putExtra("phone", fullPhoneNumber);
                        intent.putExtra("PhoneNumber", Objects.requireNonNull(edtPhone.getText()).toString());
                        intent.putExtra("verificationId", verificationId);
                        startActivity(intent);


                        progressBar.setVisibility(View.GONE);
                        signIn.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public interface OnOtpSentListener {
        void onOtpSent(String verificationId, String phoneNumber);
    }
}




