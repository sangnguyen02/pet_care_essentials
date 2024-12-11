package com.example.uiux.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.User.Review.SupplyReviewActivity;
import com.example.uiux.Adapters.BestSellerAdapter;
import com.example.uiux.Adapters.SameSuppliesAdapter;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Adapters.SupplyDetailOptionAdapter;
import com.example.uiux.Adapters.SupplyImageListAdapter;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SupplyDetailActivity extends AppCompatActivity implements SupplyDetailOptionAdapter.OnSupplyOptionClickListener {
    MaterialCardView bottomSheetAddToCart, mcv_minus, mcv_plus, mcv_supply_review;
    MaterialButton btn_confirm_add_to_cart;
    BottomSheetBehavior bottomSheetBehaviorAddToCart;
    LottieAnimationView aniLove;
    ImageView img_back, img_supply, img_arrow, img_supply_add_to_cart;
    MaterialCardView mcv_description, mcv_add_to_cart;
    RecyclerView rcv_img_list, rcv_option_list, rcv_same_supplies;
    SupplyImageListAdapter supplyImageListAdapter;
    List<String> imageUrls  = new ArrayList<>();
    List<Supplies_Review> suppliesReviews;
    TextView tv_supply_title, tv_supply_description, tv_supply_price, tv_price_add_to_cart, tv_total_stock_quantity, tv_quantity_of_supply, tv_rating_supply_detail;
    DatabaseReference databaseReference, detailRef, cartRef, reviewRef;;
    String accountId;
    String supplyId;
    String supplyAddToCartImageUrl;
//    FirebaseAuth mAuth;
    SupplyDetailOptionAdapter supplyDetailOptionAdapter;
    boolean showMoreClick = false;
    boolean isAnimating = false;
    boolean isOptionSelected = false;
    ProgressBar progressBar_sameSupplies;
    List<Supplies> sameSuppliesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_supply_detail);
//        mAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        initWidget();
        // Lấy supply_id từ Intent
        supplyId = getIntent().getStringExtra("supply_id");
        if (supplyId != null) {
            loadSupplyDetails(supplyId);
            loadSupplyImages(supplyId);
        } else {
            Toast.makeText(this, "Supply ID not found.", Toast.LENGTH_SHORT).show();
        }

        // Set trạng thái ẩn đi ở onCreate()
        bottomSheetBehaviorAddToCart.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehaviorAddToCart.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
//                if (i == BottomSheetBehavior.STATE_EXPANDED) {
//
//                } else if (i == BottomSheetBehavior.STATE_COLLAPSED) {
//
//                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                view.setAlpha(0.5f + (v * 0.5f));
                view.setScaleX(0.9f + (0.1f * v));
                view.setScaleY(0.9f + (0.1f * v));

                view.animate()
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(300)
                        .start();
            }
        });

        mcv_supply_review.setOnClickListener(view -> {
            Intent gotoReview = new Intent(SupplyDetailActivity.this, SupplyReviewActivity.class);
            gotoReview.putExtra("supplyId", supplyId);
            startActivity(gotoReview);
        });


    }

    private void initWidget() {
        aniLove = findViewById(R.id.ani_love);
        progressBar_sameSupplies = findViewById(R.id.progressBar_sameSupplies);
        tv_rating_supply_detail = findViewById(R.id.tv_rating_supply_detail);
        tv_supply_title = findViewById(R.id.tv_supply_title);
        tv_supply_price = findViewById(R.id.tv_supply_price);
        tv_supply_description = findViewById(R.id.tv_description);
        mcv_supply_review = findViewById(R.id.mcv_supply_review);

        // Bottomsheet
        tv_price_add_to_cart = findViewById(R.id.tv_price_add_to_cart);
        tv_total_stock_quantity = findViewById(R.id.tv_total_stock_quantity);
        mcv_minus = findViewById(R.id.mcv_minus);
        mcv_plus = findViewById(R.id.mcv_plus);
        tv_quantity_of_supply = findViewById(R.id.tv_quantity_of_supply);
        btn_confirm_add_to_cart = findViewById(R.id.btn_confirm_add_to_cart);

        img_back = findViewById(R.id.img_back_supply_detail);
        img_arrow = findViewById(R.id.img_arrow);
        img_supply = findViewById(R.id.img_thumpnail);
        img_supply_add_to_cart = findViewById(R.id.img_add_to_cart);
        bottomSheetAddToCart = findViewById(R.id.bottom_sheet_add_to_cart);
        bottomSheetBehaviorAddToCart = BottomSheetBehavior.from(bottomSheetAddToCart);
        rcv_img_list = findViewById(R.id.rcv_img_list);
        rcv_img_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        supplyImageListAdapter = new SupplyImageListAdapter(imageUrls, this);
        rcv_img_list.setAdapter(supplyImageListAdapter);

        supplyImageListAdapter.setImageClickListener(imageUrl -> {
            if (!isDestroyed()) {
                Glide.with(SupplyDetailActivity.this)
                        .load(imageUrl)
                        .placeholder(R.drawable.product_sample)
                        .error(R.drawable.product_sample)
                        .into(img_supply);
            }
        });

        rcv_option_list = findViewById(R.id.rcv_option_list);
        rcv_same_supplies = findViewById(R.id.rcv_same_supplies);


        mcv_description = findViewById(R.id.mcv_description);
        mcv_add_to_cart = findViewById(R.id.mcv_add_to_cart);

        mcv_add_to_cart.setOnClickListener(view -> {
            if(!supplyId.isEmpty()) {
                bottomSheetAddToCart.setVisibility(View.VISIBLE);
                bottomSheetBehaviorAddToCart.setState(BottomSheetBehavior.STATE_EXPANDED);
                loadSupplyOptions(supplyId);
            }
        });

        mcv_minus.setOnClickListener(view -> {
            if (!isOptionSelected) {
                Toast.makeText(this, "Please select an option before adjusting quantity.", Toast.LENGTH_SHORT).show();
                return;
            }
            updateQuantity(-1);
        });

        mcv_plus.setOnClickListener(view -> {
            if (!isOptionSelected) {
                Toast.makeText(this, "Please select an option before adjusting quantity.", Toast.LENGTH_SHORT).show();
                return;
            }
            updateQuantity(1);
        });

        if (Integer.parseInt(tv_quantity_of_supply.getText().toString()) <= 0) {
            mcv_minus.setEnabled(false);
            //btn_confirm_add_to_cart.setEnabled(false);
        }

        mcv_description.setOnClickListener(view -> {
            if (showMoreClick) {
                tv_supply_description.setMaxLines(5);
                img_arrow.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_down));
            } else {
                tv_supply_description.setMaxLines(Integer.MAX_VALUE);
                img_arrow.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_up));
            }
            showMoreClick = !showMoreClick;
        });


        btn_confirm_add_to_cart.setOnClickListener(view -> addToCart(supplyId));

        // Thiết lập sự kiện click cho aniLove
        aniLove.setOnClickListener(view -> {
            toggleAnimation();
        });


        img_back.setOnClickListener(view -> {
            finish();
        });



    }

    private void addToCart(String supplyId) {
        if (accountId != null) {
            int quantity = Integer.parseInt(tv_quantity_of_supply.getText().toString());
            String priceStr = tv_price_add_to_cart.getText().toString().replace(getString(R.string.currency_vn), "").trim();
            priceStr = priceStr.replace(".", "").trim();
            double price = Double.parseDouble(priceStr);
            String supplyTitle = tv_supply_title.getText().toString();
            String selectedSupplySize = supplyDetailOptionAdapter.getSelectedOptionName();
            String combinedKey = generateCombinedKey(supplyId, selectedSupplySize);
            cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(accountId).child(combinedKey);

            // Kiểm tra giỏ hàng dựa trên combinedKey
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Sản phẩm đã có trong Cart, cập nhật số lượng
                        CartItem existingCartItem = snapshot.getValue(CartItem.class);
                        if (existingCartItem != null) {
                            int newQuantity = existingCartItem.getQuantity() + quantity;
                            double newTotalPrice = newQuantity * existingCartItem.getSupply_price();
                            existingCartItem.setQuantity(newQuantity);
                            existingCartItem.setTotalPrice(newTotalPrice);
                            cartRef.setValue(existingCartItem);
                            Toast.makeText(SupplyDetailActivity.this, "Quantity updated in cart.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Sản phẩm chưa có trong Cart, thêm mới
                        CartItem newCartItem = new CartItem(supplyId, supplyTitle, selectedSupplySize, price, quantity, quantity * price, supplyAddToCartImageUrl);
                        cartRef.setValue(newCartItem);
                        Toast.makeText(SupplyDetailActivity.this, "Added to cart.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }


    private String generateCombinedKey(String supplyId, String selectedSupplySize) {
        String formattedSupplySize = selectedSupplySize.replace(".", ",");
        return supplyId + "_" + formattedSupplySize;
    }


    private void updateQuantity(int change) {
        int currentQuantity = Integer.parseInt(tv_quantity_of_supply.getText().toString());
        int totalStock = Integer.parseInt(tv_total_stock_quantity.getText().toString());

        int newQuantity = currentQuantity + change;

        if (newQuantity < 0) {
            newQuantity = 0;
        } else if (newQuantity > totalStock) {
            newQuantity = totalStock;
        }

        tv_quantity_of_supply.setText(String.valueOf(newQuantity));
        mcv_minus.setEnabled(newQuantity > 0);
    }

    // Hàm lấy các thông tin khác của sản phẩm (tiêu đề, mô tả, giá)
    private void loadSupplyDetails(String supplyId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplies").child(supplyId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Supplies supplies = snapshot.getValue(Supplies.class);
                if (supplies != null) {
                    tv_supply_title.setText(supplies.getName());
                    tv_supply_description.setText(supplies.getDescription());
                    String currentCategory = supplies.getCategory();
                    String currentSuppliesId = supplies.getSupplies_id();


                    if (currentCategory != null && !currentCategory.isEmpty()) {
                        getSameSuppliesItems(currentCategory, currentSuppliesId);
                    }

                    // Format the sell price to remove decimals
                    DecimalFormat df = new DecimalFormat("#");
                    double sellPrice = Double.valueOf(Objects.requireNonNull(supplies.getSell_price()));

                    // Set formatted price
                    tv_supply_price.setText(CurrencyFormatter.formatCurrency(sellPrice, getString(R.string.currency_vn)));

                    // Set quantity without formatting (as it's an integer)
                    tv_total_stock_quantity.setText(String.valueOf(supplies.getQuantity()));

                    if (supplies.getImageUrls() != null && !supplies.getImageUrls().isEmpty()) {
                        supplyAddToCartImageUrl = supplies.getImageUrls().get(0);
                        if(!isDestroyed()) {
                            Glide.with(SupplyDetailActivity.this)
                                    .load(supplyAddToCartImageUrl)
                                    .into(img_supply_add_to_cart);
                        }

                    }

                    loadSupplyReview(supplyId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupplyDetailActivity.this, "Failed to load supply details.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadSupplyOptions(String supplyId) {
        detailRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports").child(supplyId);
        detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Supplies_Detail> supplyDetailOptions = new ArrayList<>();
                // Duyệt qua các child của snapshot
                for (DataSnapshot optionSnapshot : snapshot.child("suppliesDetail").getChildren()) {
                    Supplies_Detail option = optionSnapshot.getValue(Supplies_Detail.class);
                    if (option != null) {
                        supplyDetailOptions.add(option);
                    }
                }
                // Khởi tạo adapter và set cho RecyclerView
                supplyDetailOptionAdapter = new SupplyDetailOptionAdapter(supplyDetailOptions, SupplyDetailActivity.this, SupplyDetailActivity.this);
                rcv_option_list.setLayoutManager(new GridLayoutManager(SupplyDetailActivity.this, 3));
                rcv_option_list.setAdapter(supplyDetailOptionAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupplyDetailActivity.this, "Failed to load supply options.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSupplyOptionClick(double costPrice, int stockRemain) {
        tv_price_add_to_cart.setText(CurrencyFormatter.formatCurrency(costPrice, getString(R.string.currency_vn)));
        tv_total_stock_quantity.setText(String.valueOf(stockRemain));
        isOptionSelected = true;
        mcv_minus.setEnabled(true);
        mcv_plus.setEnabled(true);
        //btn_confirm_add_to_cart.setEnabled(true);

        if (tv_quantity_of_supply.getText().equals("0")) {
            mcv_minus.setEnabled(false);
            //btn_confirm_add_to_cart.setEnabled(false);
        }
    }


    private void loadSupplyImages(String supplyId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplies").child(supplyId).child("imageUrls");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageUrls.clear();
                for (DataSnapshot imgSnapshot : snapshot.getChildren()) {
                    String imageUrl = imgSnapshot.getValue(String.class);
                    imageUrls.add(imageUrl);

                }
                supplyImageListAdapter.notifyDataSetChanged();

                if (!imageUrls.isEmpty()) {
                    Glide.with(SupplyDetailActivity.this)
                            .load(imageUrls.get(0))
                            .error(R.drawable.guest)
                            .into(img_supply);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupplyDetailActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show();
            }
        });

        supplyImageListAdapter.setImageClickListener(imageUrl -> {
            Glide.with(SupplyDetailActivity.this)
                    .load(imageUrl)
                    .error(R.drawable.guest)
                    .into(img_supply); // Load selected image
        });
    }

    private void toggleAnimation() {
        if (!isAnimating) {
            aniLove.setMinAndMaxFrame(0, 50);
            aniLove.setProgress(0f);
            aniLove.playAnimation();
            isAnimating = true;
        } else {
            aniLove.setProgress(0);
            aniLove.pauseAnimation();
            isAnimating = false;
        }
    }

    void loadSupplyReview(String supplyId) {
        if (suppliesReviews == null) {
            suppliesReviews = new ArrayList<>();
        }
        reviewRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");
        reviewRef.orderByChild("supplies_id").equalTo(supplyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suppliesReviews.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies_Review suppliesReview = snapshot.getValue(Supplies_Review.class);
                    if (suppliesReview != null) {
                        suppliesReviews.add(suppliesReview);
                    }
                }
               // no_of_review.setText(String.valueOf(suppliesReviews.size()));
                updateReviewStats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
            }
        });
    }

    private void getSameSuppliesItems(String category, String currentSuppliesId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
        ref.orderByChild("category").equalTo(category).limitToFirst(6).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sameSuppliesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supply = snapshot.getValue(Supplies.class);
                    if (supply != null && !supply.getSupplies_id().equals(currentSuppliesId)) {
                        sameSuppliesList.add(supply);
                    }
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

                        progressBar_sameSupplies.setVisibility(View.GONE);
                        SameSuppliesAdapter adapter = new SameSuppliesAdapter(sameSuppliesList, reviewList, SupplyDetailActivity.this);
                        rcv_same_supplies.setLayoutManager(new LinearLayoutManager(SupplyDetailActivity.this ,LinearLayoutManager.HORIZONTAL,false));
                        rcv_same_supplies.setAdapter(adapter);
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

    void updateReviewStats() {
        float totalRating = 0;
        int totalReviews = suppliesReviews.size();

        if (totalReviews == 0) {
            tv_rating_supply_detail.setText("0.0");
            return;
        }

        for (Supplies_Review review : suppliesReviews) {
            int rating = review.getRating();
            Log.e("Rating", String.valueOf(rating));
            totalRating += rating;


        }

        float averageRating = totalRating / totalReviews;
        tv_rating_supply_detail.setText(String.format(Locale.US, "%.1f", averageRating));
    }




}