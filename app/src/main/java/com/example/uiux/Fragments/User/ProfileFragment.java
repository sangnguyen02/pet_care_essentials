package com.example.uiux.Fragments.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.PhoneUpdateProfileActivity;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    View rootView;
    String phone;
    CircleImageView img_avatar, img_setting;
    TextView tv_username;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initWidget();

        // Lấy số điện thoại từ arguments
        Bundle args = getArguments();
        if (args != null) {
            phone = args.getString("phone_number"); // Sử dụng cùng key "phone_number"
            Log.d("ProfileFragment", "Phone Number: " + phone); // In ra log để kiểm tra
        }

        // Event Img Avatar
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), PhoneUpdateProfileActivity.class);
                intent.putExtra("phone_no", phone);
                startActivity(intent);
            }
        });

        loadUserProfile();



        return rootView;
    }

    void initWidget() {
        img_avatar = rootView.findViewById(R.id.img_avatar);
        img_setting = rootView.findViewById(R.id.img_setting);
        tv_username = rootView.findViewById(R.id.tv_username);
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

                            tv_username.setText(account.getFullname() != null ? account.getFullname() : "Chưa cập nhật");
                            // Load image if it exists
                            if (account.getImage() != null) {
                                Glide.with(rootView.getContext())
                                        .load(account.getImage())
                                        .into(img_avatar);
                            }
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