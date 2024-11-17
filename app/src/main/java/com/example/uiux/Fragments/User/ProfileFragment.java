package com.example.uiux.Fragments.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.CartActivity;
import com.example.uiux.Activities.User.Pet.UpdatePetInfoActivity;
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
    MaterialCardView mcv_help_center, mcv_pet_profile;
    String phone;
    String accountId;
    CircleImageView img_avatar, img_setting;
    ImageView img_cart_at_profile, img_red_circle_at_profile;
    TextView tv_username, tv_number_of_cart_item_at_profile;
    DatabaseReference databaseReference;
    DatabaseReference cartItems;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.primary));
        }
        preferences =  getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);

        // Lấy số điện thoại từ arguments
        Bundle args = getArguments();
        if (args != null) {
            phone = args.getString("phone_number"); // Sử dụng cùng key "phone_number"
            Log.d("ProfileFragment", "Phone Number: " + phone); // In ra log để kiểm tra
        }

        initWidget();
        displayNoOfCartItem();
        loadUserProfile();

        img_avatar.setOnClickListener(view -> {
            Intent intent = new Intent(rootView.getContext(), PhoneUpdateProfileActivity.class);
            intent.putExtra("phone_no", phone);
            startActivity(intent);
        });

        img_setting.setOnClickListener(view -> {
            if (accountId != null) {
                Intent intent = new Intent(rootView.getContext(), SettingsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(rootView.getContext(), "Account ID is not available", Toast.LENGTH_SHORT).show();
            }
        });

        img_cart_at_profile.setOnClickListener(view -> {
            Intent intent = new Intent(rootView.getContext(), CartActivity.class);
            startActivity(intent);
        });

        mcv_help_center.setOnClickListener(view -> {

        });

        mcv_pet_profile.setOnClickListener(view -> {
            Intent goToPetProfile = new Intent(rootView.getContext(), UpdatePetInfoActivity.class);
            startActivity(goToPetProfile);
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // Gọi lại để get dữ liệu mới sau khi quay lại từ màn hình khác
    }

    void initWidget() {
        tv_number_of_cart_item_at_profile = rootView.findViewById(R.id.tv_number_of_cart_item_at_profile);
        img_cart_at_profile = rootView.findViewById(R.id.img_cart_at_profile);
        img_red_circle_at_profile = rootView.findViewById(R.id.img_red_circle_at_profile);
        img_avatar = rootView.findViewById(R.id.img_avatar);
        img_setting = rootView.findViewById(R.id.img_setting);
        tv_username = rootView.findViewById(R.id.tv_username);
        mcv_help_center = rootView.findViewById(R.id.mcv_help_center);
        mcv_pet_profile = rootView.findViewById(R.id.mcv_pet_profile);
    }

    void displayNoOfCartItem() {
        cartItems = FirebaseDatabase.getInstance().getReference("Cart").child(accountId);
        cartItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Đếm số lượng item trong giỏ
                long itemCount = dataSnapshot.getChildrenCount();

                // Kiểm tra và cập nhật giao diện tùy vào số lượng item
                if (itemCount > 0) {
                    // Hiển thị số lượng item trong giỏ
                    tv_number_of_cart_item_at_profile.setVisibility(View.VISIBLE);
                    tv_number_of_cart_item_at_profile.setText(String.valueOf(itemCount));
                    img_cart_at_profile.setVisibility(View.VISIBLE);
                } else {
                    tv_number_of_cart_item_at_profile.setVisibility(View.GONE);
                    img_cart_at_profile.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có (tuỳ theo yêu cầu của bạn)
            }
        });
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