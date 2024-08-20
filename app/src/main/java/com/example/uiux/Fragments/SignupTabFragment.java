package com.example.uiux.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uiux.R;

public class SignupTabFragment extends Fragment {
    View rootView;
    EditText edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.signup_tab_fragment, container, false);

        initWidget();

        return rootView;
    }

    void initWidget() {
        edtPhone = rootView.findViewById(R.id.edt_phone_number_sign_up);
        edtEmail = rootView.findViewById(R.id.edt_email);
        edtPassword = rootView.findViewById(R.id.edt_password_sign_up);
        edtConfirmPassword = rootView.findViewById(R.id.edt_confirm_password);
    }
}
