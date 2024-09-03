package com.example.uiux.Fragments.User;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.uiux.Adapters.BannerAdapter;
import com.example.uiux.Adapters.CategoryAdapter;
import com.example.uiux.Models.Banner;
import com.example.uiux.Models.Category;
import com.example.uiux.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
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
    RecyclerView rcv_category;
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
        rcv_category.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        getBannerImages();
        getCategoryImages();
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Categories");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<Category> categoryList = new ArrayList<>();
            for (StorageReference fileRef : listResult.getItems()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageName = fileRef.getName().split("\\.")[0]; // Assuming the file name without extension is the category name
                    categoryList.add(new Category(uri.toString(), imageName));

                    // Check if all categories are loaded before updating the adapter
                    if (categoryList.size() == listResult.getItems().size()) {
                        progressBar_category.setVisibility(View.GONE);
                        mListCategory = categoryList;
                        CategoryAdapter adapter = new CategoryAdapter(mListCategory);
                        rcv_category.setAdapter(adapter);
                    }
                });
            }
        }).addOnFailureListener(exception -> {
            // Handle errors
        });
    }

}