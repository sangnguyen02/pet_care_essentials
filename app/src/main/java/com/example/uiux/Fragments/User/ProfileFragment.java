package com.example.uiux.Fragments.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.Profile.PhoneUpdateProfileActivity;
import com.example.uiux.Activities.User.Profile.SettingsActivity;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    View rootView;
    MaterialCardView mcv_help_center;
    String phone;
    String accountId;
    CircleImageView img_avatar, img_setting;
    TextView tv_username;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.primary));
        }
        // Lấy số điện thoại từ arguments
        Bundle args = getArguments();
        if (args != null) {
            phone = args.getString("phone_number"); // Sử dụng cùng key "phone_number"
            Log.d("ProfileFragment", "Phone Number: " + phone); // In ra log để kiểm tra
        }

        initWidget();
        loadUserProfile();

        img_avatar.setOnClickListener(view -> {
            Intent intent = new Intent(rootView.getContext(), PhoneUpdateProfileActivity.class);
            intent.putExtra("phone_no", phone);
            startActivity(intent);
        });

        img_setting.setOnClickListener(view -> {
            if (accountId != null) {
                Intent intent = new Intent(rootView.getContext(), SettingsActivity.class);
                intent.putExtra("phone_no", phone);
                intent.putExtra("account_id", accountId);
                startActivity(intent);
            } else {
                Toast.makeText(rootView.getContext(), "Account ID is not available", Toast.LENGTH_SHORT).show();
            }
        });

        mcv_help_center.setOnClickListener(view -> {

        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // Gọi lại để get dữ liệu mới sau khi quay lại từ màn hình khác
    }

    void initWidget() {
        img_avatar = rootView.findViewById(R.id.img_avatar);
        img_setting = rootView.findViewById(R.id.img_setting);
        tv_username = rootView.findViewById(R.id.tv_username);
        mcv_help_center = rootView.findViewById(R.id.mcv_help_center);
    }

    private void loadUserProfile() {

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
                            accountId = snapshot.getKey();
                            tv_username.setText(account.getFullname() != null ? account.getFullname() : "Chưa cập nhật");
                            // Load image if it exists
                            if (account.getImage() != null) {
                                Glide.with(rootView.getContext())
                                        .load(account.getImage())
                                        .into(img_avatar);
                            }
                            Log.d("ProfileFragment", "Account ID: " + accountId); // Log giá trị accountId

                        }
                    }
                } else {
                    // If the user doesn't exist, prompt them to create a profile
                    Toast.makeText(rootView.getContext(), "No existing profile found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(rootView.getContext(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}