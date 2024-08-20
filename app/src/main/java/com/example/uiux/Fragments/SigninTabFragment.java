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

public class SigninTabFragment extends Fragment {
    View rootView;
    EditText edtPhone, edtPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.signin_tab_fragment, container, false);

        initWidget();

        return rootView;
    }

    void initWidget() {
        edtPhone = rootView.findViewById(R.id.edt_phone_number);
        edtPassword = rootView.findViewById(R.id.edt_password);
    }
}
