package com.example.uiux.Fragments.User;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.uiux.Activities.AllSuppliesActivity;
import com.example.uiux.Activities.User.CartActivity;
import com.example.uiux.Activities.User.ChatBotActivity;
import com.example.uiux.Activities.User.SearchActivity;
import com.example.uiux.Adapters.BannerAdapter;
import com.example.uiux.Adapters.BestSellerAdapter;
import com.example.uiux.Adapters.CategoryAdapter;
import com.example.uiux.Model.Banner;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Image;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    View rootView;
    SearchView search_view;
    LottieAnimationView fab_chatbot;
    TextView tv_number_of_cart_item, animated_text_hint, tv_see_all_bestSeller;
    ImageView cart, iv_red_circle;
    ViewPager2 mViewPager2;
    DotsIndicator dotsIndicator;
    List<Banner> mListBanner;
    List<Category> mListCategory;
    List<Supplies> mListBestSeller;
    List<Supplies_Review> mListSuppliesReview;
    RecyclerView rcv_category, rcv_best_seller;
    ProgressBar progressBar_banner, progressBar_category, progressBar_bestSeller;
    DatabaseReference cartItems;
    SharedPreferences preferences;
    String accountId;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewPager2.getCurrentItem() == mListBanner.size() - 1) {
                mViewPager2.setCurrentItem(0);
            } else {
                mViewPager2.setCurrentItem(mViewPager2.getCurrentItem() + 1);
            }
        }
    };
    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.light_bg));
        }

        preferences =  getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);

        initWidget();
        displayNoOfCartItem();
        animatedText();

        return rootView;
    }

    void initWidget() {
        tv_see_all_bestSeller = rootView.findViewById(R.id.tv_see_all_bestSeller);
        tv_see_all_bestSeller.setOnClickListener(view -> {
            Intent goToAllProduct = new Intent(rootView.getContext(), AllSuppliesActivity.class);
            startActivity(goToAllProduct);
        });
        fab_chatbot = rootView.findViewById(R.id.fab_chatbot);
        fab_chatbot.setOnClickListener(view -> {
            Intent goToChat = new Intent(rootView.getContext(), ChatBotActivity.class);
            startActivity(goToChat);
        });
        search_view = rootView.findViewById(R.id.search_view);
        animated_text_hint = rootView.findViewById(R.id.animated_text_hint);
        cart = rootView.findViewById(R.id.iv_cart);
        iv_red_circle = rootView.findViewById(R.id.iv_red_circle);
        tv_number_of_cart_item = rootView.findViewById(R.id.tv_number_of_cart_item);
        progressBar_banner = rootView.findViewById(R.id.progressBar_banners);
        progressBar_category = rootView.findViewById(R.id.progressBar_categories);
        progressBar_bestSeller = rootView.findViewById(R.id.progressBar_bestSeller);
        mViewPager2 = rootView.findViewById(R.id.viewPager2);
        dotsIndicator = rootView.findViewById(R.id.dot_indicators);
        rcv_category = rootView.findViewById(R.id.rcv_categories);
        rcv_category.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
        rcv_best_seller = rootView.findViewById(R.id.rcv_bestSeller);
        rcv_best_seller.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        getBannerImages();
        getCategoryImages();
        getBestSellerItems();
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 5000);
            }
        });

        cart.setOnClickListener(view -> {
            Intent intent = new Intent(rootView.getContext(), CartActivity.class);
            startActivity(intent);
        });

        search_view.setOnClickListener(view -> {
            Intent intent = new Intent(rootView.getContext(), SearchActivity.class);
//            ActivityOptions options = ActivityOptions.makeCustomAnimation(
//                    getContext(),
//                    android.R.anim.slide_out_right,
//                    android.R.anim.slide_in_left
//            );
//            startActivity(intent, options.toBundle());
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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
                    tv_number_of_cart_item.setVisibility(View.VISIBLE);
                    tv_number_of_cart_item.setText(String.valueOf(itemCount));
                    iv_red_circle.setVisibility(View.VISIBLE);
                } else {
                    tv_number_of_cart_item.setVisibility(View.GONE);
                    iv_red_circle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có (tuỳ theo yêu cầu của bạn)
            }
        });
    }

    void animatedText() {
        String hintText = "Search for your pet...";
        animated_text_hint.setText("");
        animated_text_hint.setVisibility(View.VISIBLE);
        int delay = 150;
        for (int i = 0; i <= hintText.length(); i++) {
            final int index = i;
            animated_text_hint.postDelayed(() -> {
                animated_text_hint.setText(hintText.substring(0, index));
                if (index == hintText.length()) {
                    animated_text_hint.setVisibility(View.GONE);
                    search_view.setQueryHint(hintText);
                }
            }, delay * i);
        }
    }

    private void getBannerImages() {
        progressBar_banner.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Banners");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<Banner> bannerList = new ArrayList<>();
            for (StorageReference fileRef : listResult.getItems()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    bannerList.add(new Banner(uri.toString()));

                    // Kiểm tra nếu tất cả các banner đã được tải xong thì cập nhật adapter
                    if (bannerList.size() == listResult.getItems().size()) {
                        progressBar_banner.setVisibility(View.GONE);
                        mListBanner = bannerList;
                        BannerAdapter adapter = new BannerAdapter(mListBanner);
                        mViewPager2.setAdapter(adapter);
                        dotsIndicator.attachTo(mViewPager2);
                    }
                });
            }
        }).addOnFailureListener(exception -> {
            // Xử lý lỗi
        });
    }

    private void getCategoryImages() {
        progressBar_category.setVisibility(View.VISIBLE);

        // Lấy dữ liệu từ Firebase Realtime Database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu Category từ Firebase
                    Category category = snapshot.getValue(Category.class);

                    // Thêm category vào danh sách
                    categoryList.add(category);
                }

                // Khi tất cả các category được tải xong thì cập nhật adapter
                progressBar_category.setVisibility(View.GONE);
                mListCategory = categoryList;
                CategoryAdapter adapter = new CategoryAdapter(mListCategory, categoryName -> {
                    Intent intent = new Intent(getContext(), AllSuppliesActivity.class);
                    intent.putExtra("selectedCategory", categoryName);
                    startActivity(intent);
                });
                rcv_category.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
    private void getBestSellerItems() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
        ref.limitToFirst(6).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListBestSeller = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supply = snapshot.getValue(Supplies.class);
                    mListBestSeller.add(supply);
                }

                // Lấy dữ liệu đánh giá
                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");
                reviewsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Supplies_Review> reviewList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Supplies_Review review = snapshot.getValue(Supplies_Review.class);
                            reviewList.add(review);
                        }

                        // Cập nhật Adapter với danh sách sản phẩm và đánh giá
                        progressBar_bestSeller.setVisibility(View.GONE);
                        BestSellerAdapter adapter = new BestSellerAdapter(mListBestSeller, reviewList, getContext());
                        rcv_best_seller.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }




}