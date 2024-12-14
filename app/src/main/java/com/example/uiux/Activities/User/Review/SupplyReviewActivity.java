package com.example.uiux.Activities.User.Review;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.SuppliesReviewAdapter;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupplyReviewActivity extends AppCompatActivity {

    TextView no_of_review, label_no_of_review, no_review_5_star, no_review_4_star,
            no_review_3_star, no_review_2_star, no_review_1_star, average_rating_point;

    EditText edt_review;
    MaterialButton btn_send_review;
    RatingBar ratingBar_review;
    ImageView img_back_review;
    RecyclerView rcv_reviews;
    FloatingActionButton btn_write_review;
    MaterialCardView bottomSheetAddReview;
    BottomSheetBehavior bottomSheetBehaviorAddReview;
    SuppliesReviewAdapter suppliesReviewAdapter;
    List<Supplies_Review> suppliesReviews;
    DatabaseReference reviewRef;
    SharedPreferences preferences;
    String accountId , supplyId, no_of_reviews;
    Boolean fromSupplyDetail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_supply_review);

        supplyId = getIntent().getStringExtra("supplyId");
        if (supplyId != null) {
            // Sử dụng supplyId để tải dữ liệu hoặc thực hiện các hành động cần thiết
            Log.d("SupplyReviewActivity", "Received supplyId: " + supplyId);
        } else {
            Log.e("SupplyReviewActivity", "No supplyId received!");
        }





        initWidget();
        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        reviewRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");
        loadSupplyReview();
        bottomSheetBehaviorAddReview.setState(BottomSheetBehavior.STATE_HIDDEN);


    }

    void initWidget() {
        img_back_review = findViewById(R.id.img_back_review);
        img_back_review.setOnClickListener(view -> finish());
        no_of_review = findViewById(R.id.no_of_review);
        label_no_of_review = findViewById(R.id.label_no_of_review);
        no_review_5_star = findViewById(R.id.no_review_5_star);
        no_review_4_star = findViewById(R.id.no_review_4_star);
        no_review_3_star = findViewById(R.id.no_review_3_star);
        no_review_2_star = findViewById(R.id.no_review_2_star);
        no_review_1_star = findViewById(R.id.no_review_1_star);
        average_rating_point = findViewById(R.id.average_rating_point);

        suppliesReviews = new ArrayList<>();
        rcv_reviews = findViewById(R.id.rcv_reviews);
        suppliesReviewAdapter = new SuppliesReviewAdapter(suppliesReviews, this);
        rcv_reviews.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rcv_reviews.setAdapter(suppliesReviewAdapter);


        bottomSheetAddReview = findViewById(R.id.bottom_sheet_add_review);
        bottomSheetBehaviorAddReview = BottomSheetBehavior.from(bottomSheetAddReview);

        btn_write_review = findViewById(R.id.btn_write_review);
        btn_write_review.setOnClickListener(view -> {
            bottomSheetBehaviorAddReview.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        edt_review = findViewById(R.id.edt_review);
        btn_send_review = findViewById(R.id.btn_send_review);
        ratingBar_review = findViewById(R.id.ratingBar_review);

        btn_send_review.setOnClickListener(view -> {
            sendReview();
        });

        fromSupplyDetail = getIntent().getBooleanExtra("fromSupplyDetail", false);
        if(fromSupplyDetail) {
            btn_write_review.setVisibility(View.GONE);
        }

    }
    void sendReview() {
        // Lấy dữ liệu từ UI
        int rating = (int) ratingBar_review.getRating();
        String comment = edt_review.getText().toString().trim();

        if (rating == 0) {
            edt_review.setError("Vui lòng chọn số sao");
            return;
        }
        if (comment.isEmpty()) {
            edt_review.setError("Vui lòng nhập nội dung đánh giá");
            return;
        }

        // Tạo ID cho review mới
        Date datePosted = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(datePosted);

        // Kiểm tra nếu đã có đánh giá của người dùng này đối với sản phẩm này
        reviewRef.orderByChild("account_id").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean reviewExists = false;
                String existingReviewId = null;

                // Duyệt qua các review để kiểm tra xem đã có review của người dùng này chưa
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies_Review existingReview = snapshot.getValue(Supplies_Review.class);
                    if (existingReview != null && existingReview.getSupplies_id().equals(supplyId)) {
                        // Nếu có, lưu lại reviewId để update
                        reviewExists = true;
                        existingReviewId = snapshot.getKey();
                        break;
                    }
                }

                if (reviewExists) {
                    // Cập nhật review hiện tại
                    Supplies_Review updatedReview = new Supplies_Review(
                            existingReviewId,
                            accountId,
                            supplyId,
                            rating,
                            comment,
                            formattedDate,
                            true
                    );

                    reviewRef.child(existingReviewId).setValue(updatedReview).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            edt_review.setText("");
                            ratingBar_review.setRating(0);
                            loadSupplyReview();
                            bottomSheetBehaviorAddReview.setState(BottomSheetBehavior.STATE_HIDDEN);
                            showToast("Đánh giá đã được cập nhật!");
                        } else {
                            showToast("Đã có lỗi xảy ra, vui lòng thử lại!");
                        }
                    });

                } else {
                    // Nếu chưa có review, tạo review mới
                    String reviewId = reviewRef.push().getKey();
                    Supplies_Review newReview = new Supplies_Review(
                            reviewId,
                            accountId,
                            supplyId,
                            rating,
                            comment,
                            formattedDate,
                            true
                    );

                    reviewRef.child(reviewId).setValue(newReview).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            edt_review.setText("");
                            ratingBar_review.setRating(0);
                            loadSupplyReview();
                            bottomSheetBehaviorAddReview.setState(BottomSheetBehavior.STATE_HIDDEN);
                            showToast("Đánh giá đã được gửi!");
                        } else {
                            showToast("Đã có lỗi xảy ra, vui lòng thử lại!");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
            }
        });
    }


    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    void loadSupplyReview() {
        if (suppliesReviews == null) {
            suppliesReviews = new ArrayList<>();
        }
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
                suppliesReviewAdapter.notifyDataSetChanged();
                no_of_review.setText(String.valueOf(suppliesReviews.size()));
                updateReviewStats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
            }
        });
    }

    void updateReviewStats() {
        int[] starCounts = new int[5];
        float totalRating = 0;
        int totalReviews = suppliesReviews.size();

        if (totalReviews == 0) {
            no_review_5_star.setText("0");
            no_review_4_star.setText("0");
            no_review_3_star.setText("0");
            no_review_2_star.setText("0");
            no_review_1_star.setText("0");
            average_rating_point.setText("0.0");
            return;
        }

        for (Supplies_Review review : suppliesReviews) {
            int rating = review.getRating();
            Log.e("Rating", String.valueOf(rating));
            totalRating += rating;

            if (rating == 5) {
                starCounts[4]++;
            } else if (rating == 4) {
                starCounts[3]++;
            } else if (rating == 3) {
                starCounts[2]++;
            } else if (rating == 2) {
                starCounts[1]++;
            } else if (rating == 1) {
                starCounts[0]++;
            }
        }

        no_review_5_star.setText(String.valueOf(starCounts[4]));
        no_review_4_star.setText(String.valueOf(starCounts[3]));
        no_review_3_star.setText(String.valueOf(starCounts[2]));
        no_review_2_star.setText(String.valueOf(starCounts[1]));
        no_review_1_star.setText(String.valueOf(starCounts[0]));

        float averageRating = totalRating / totalReviews;
        average_rating_point.setText(String.format(Locale.US, "%.1f", averageRating));
    }


}