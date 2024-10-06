package com.example.uiux.Fragments.User;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import java.util.List;

public class HomeFragment extends Fragment {
    View rootView;
    ImageView bell;
    ViewPager2 mViewPager2;
    DotsIndicator dotsIndicator;
    List<Banner> mListBanner;
    List<Category> mListCategory;
    List<Supplies> mListBestSeller;
    List<Supplies_Review> mListSuppliesReview;
    RecyclerView rcv_category, rcv_best_seller;
    ProgressBar progressBar_banner, progressBar_category, progressBar_bestSeller;
    private Handler mHandler = new Handler();
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
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        initWidget();

        return rootView;
    }

    void initWidget() {
        bell = rootView.findViewById(R.id.iv_bell);
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
                CategoryAdapter adapter = new CategoryAdapter(mListCategory);
                rcv_category.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
    private void getBestSellerItems() {
        // Assuming you have already initialized mListBestSeller
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListBestSeller = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supply = snapshot.getValue(Supplies.class);
                    mListBestSeller.add(supply);
                }

                // Get reviews
                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");
                reviewsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Supplies_Review> reviewList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Supplies_Review review = snapshot.getValue(Supplies_Review.class);
                            reviewList.add(review);
                        }

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