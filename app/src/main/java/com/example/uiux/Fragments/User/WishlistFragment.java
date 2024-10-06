package com.example.uiux.Fragments.User;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uiux.R;

public class WishlistFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        return rootView;
    }
}