package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class SuppliesReviewAdapter  extends RecyclerView.Adapter<SuppliesReviewAdapter.SuppliesReviewViewHolder>{
    private List<Supplies_Review> suppliesReviewList;
    private Context context;
    public SuppliesReviewAdapter(List<Supplies_Review> suppliesReviewList, Context context) {
        this.suppliesReviewList = suppliesReviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public SuppliesReviewAdapter.SuppliesReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new SuppliesReviewAdapter.SuppliesReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliesReviewAdapter.SuppliesReviewViewHolder holder, int position) {
        Supplies_Review suppliesReview = suppliesReviewList.get(position);
        holder.bind(suppliesReview);
    }

    @Override
    public int getItemCount() {
        return suppliesReviewList.size();
    }
    class SuppliesReviewViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img_avatar_review;
        private TextView tv_review_username, tv_review_content, tv_review_date;
        private RatingBar ratingBar_user;

        public SuppliesReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_review_username = itemView.findViewById(R.id.tv_review_username);
            tv_review_content = itemView.findViewById(R.id.tv_review_content);
            tv_review_date = itemView.findViewById(R.id.tv_review_date);
            ratingBar_user=itemView.findViewById(R.id.ratingBar_user);
            img_avatar_review = itemView.findViewById(R.id.img_avatar_review);

        }
        public void bind(Supplies_Review suppliesReview) {
            tv_review_content.setText(suppliesReview.getComment());
            tv_review_date.setText(suppliesReview.getDate_posted());
            int ratingPoint = suppliesReview.getRating();
//            ratingBar_user.setRating(Float.parseFloat(String.valueOf(ratingPoint)));
            ratingBar_user.setNumStars(ratingPoint);
            String account_id = suppliesReview.getAccount_id();
            loadUserProfile(account_id);

        }

        private void loadUserProfile(String account_id) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Account");

            databaseReference.child(account_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        Model.Account account = dataSnapshot.getValue(Model.Account.class);


                        if (account != null) {
                            tv_review_username.setText(account.getFullname() != null ? account.getFullname() : "Not updated yet");

                            if (account.getImage() != null && !account.getImage().isEmpty()) {
                                Glide.with(context)
                                        .load(account.getImage())
                                        .into(img_avatar_review);
                            } else {
                                Glide.with(context)
                                        .load(R.drawable.user)
                                        .into(img_avatar_review);
                            }
                        }
                    } else {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(context, "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}
