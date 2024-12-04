package com.example.uiux.Activities.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.uiux.Fragments.User.CaringFragment;
import com.example.uiux.Fragments.User.HomeFragment;
import com.example.uiux.Fragments.User.ProfileFragment;
import com.example.uiux.Fragments.User.WishlistFragment;
import com.example.uiux.R;
import com.example.uiux.databinding.ActivityMainUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityUser extends AppCompatActivity {
    ActivityMainUserBinding binding;
//    LottieAnimationView fab_chatbot;
    String phone;
    DatabaseReference accountRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initWidget();

        replaceFragment(new HomeFragment());
//        BadgeDrawable badgeDrawable = binding.bottomNavigationView.getOrCreateBadge(R.id.cart_screen);
//        badgeDrawable.setVisible(true);
//        badgeDrawable.setNumber(3);
        accountRef = FirebaseDatabase.getInstance().getReference("Account");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phone = extras.getString("phone_number");
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            preferences.edit().putString("phone_number", phone).apply();
            Log.d("Phone No Ở Main", phone);
//            Log.d("Username", userName);

            getAccountIDByPhone(phone);
        }




        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_screen:
                    replaceFragment(new HomeFragment());
                    break;
//                case R.id.cart_screen:
//                    replaceFragment(new CartFragment());
//                    break;
                case R.id.wishlist_screen:
                    replaceFragment(new WishlistFragment());
                    break;
                case R.id.caring_screen:
                    replaceFragment(new CaringFragment());
                    break;
                case R.id.profile_screen:
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("phone_number", phone);
                    profileFragment.setArguments(bundle);
                    replaceFragment(profileFragment);
                    break;

            }

            return true;
        });
    }

    void initWidget() {
//        fab_chatbot = findViewById(R.id.fab_chatbot);
    }

    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

    void getAccountIDByPhone(String phone) {
        accountRef.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                        String accountId = accountSnapshot.getKey(); // Lấy accountID
                        Integer accountType = accountSnapshot.child("account_type").getValue(Integer.class);
                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        preferences.edit().putString("accountID", accountId).apply();
                        preferences.edit().putInt("accountType", accountType).apply();
                        Log.d("Account ID", "Found Account ID: " + accountId);
                        // Bạn có thể lưu accountId hoặc sử dụng ở đây
                    }
                } else {
                    Log.d("Account ID", "No account found with this phone number.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Account ID", "Error fetching account ID: " + databaseError.getMessage());
            }
        });
    }
}