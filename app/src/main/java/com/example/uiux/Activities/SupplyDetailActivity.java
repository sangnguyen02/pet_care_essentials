package com.example.uiux.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Adapters.SupplyImageListAdapter;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SupplyDetailActivity extends AppCompatActivity {
    LottieAnimationView aniLove;
    ImageView img_back, img_supply, img_arrow;
    MaterialCardView mcv_see_more;
    RecyclerView rcv_img_list, rcv_size_list;
    SupplyImageListAdapter supplyImageListAdapter;
    List<String> imageUrls  = new ArrayList<>();
    TextView tv_supply_title, tv_supply_description, tv_supply_price, tv_show_more;
    DatabaseReference databaseReference;
    DatabaseReference detailRef;
    String supplyId;
    String currency;
    boolean showMoreClick = false;
    boolean isAnimating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_supply_detail);
        initWidget();
        // Lấy supply_id từ Intent
        currency = "VND - ";
        supplyId = getIntent().getStringExtra("supply_id");
        if (supplyId != null) {
            loadSupplyDetails(supplyId);
            loadSupplyImages(supplyId);
        } else {
            Toast.makeText(this, "Supply ID not found.", Toast.LENGTH_SHORT).show();
        }

    }

    private void initWidget() {
        aniLove = findViewById(R.id.ani_love);
        tv_supply_title = findViewById(R.id.tv_supply_title);
        tv_supply_price = findViewById(R.id.tv_supply_price);
        tv_supply_description = findViewById(R.id.tv_description);
        tv_show_more = findViewById(R.id.tv_show_more);
        img_back = findViewById(R.id.img_back_supply_detail);
        img_arrow = findViewById(R.id.img_arrow);
        img_supply = findViewById(R.id.img_thumpnail);
        rcv_img_list = findViewById(R.id.rcv_img_list);
        rcv_img_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        supplyImageListAdapter = new SupplyImageListAdapter(imageUrls, this);
        rcv_img_list.setAdapter(supplyImageListAdapter);

        mcv_see_more = findViewById(R.id.mcv_see_more);

        mcv_see_more.setOnClickListener(view -> {
            if (showMoreClick) {
                tv_supply_description.setMaxLines(5);
                tv_show_more.setText("See More");
                img_arrow.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_down));
            } else {
                // Expand the description
                tv_supply_description.setMaxLines(Integer.MAX_VALUE);
                tv_show_more.setText("Hide"); // Update text to "Hide"
                img_arrow.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_up));
            }
            // Toggle the state
            showMoreClick = !showMoreClick;
        });

        // Thiết lập sự kiện click cho aniLove
        aniLove.setOnClickListener(view -> {
            toggleAnimation();
        });


        img_back.setOnClickListener(view -> {
            finish();
        });
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
                    tv_supply_price.setText(currency + String.valueOf(supplies.getSell_price()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupplyDetailActivity.this, "Failed to load supply details.", Toast.LENGTH_SHORT).show();
            }
        });

        detailRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports_Test").child(supplyId);
        detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Supplies_Detail detail = snapshot.getValue(Supplies_Detail.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Hàm load hình ảnh của sản phẩm vào RecyclerView
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
        float maxFrame = aniLove.getMaxFrame(); // Lấy số khung hình tối đa

        if (!isAnimating) {
            // Nếu không hoạt động, chạy từ frame 0 đến frame 50
            aniLove.setMinAndMaxFrame(0, 50); // Chỉ định khoảng khung hình
            aniLove.setProgress(0f); // Thiết lập khung hình bắt đầu
            aniLove.playAnimation(); // Chạy animation
            isAnimating = true;
        } else {
            aniLove.setProgress(0); // Thiết lập khung hình bắt đầu
            // Chạy animation
            aniLove.pauseAnimation();
            isAnimating = false;
        }
    }



}