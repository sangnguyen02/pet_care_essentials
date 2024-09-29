package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuppliesAdapter  extends RecyclerView.Adapter<SuppliesAdapter.SuppliesViewHolder>{
    private List<Supplies> suppliesList;
    private Context context;
    public SuppliesAdapter(List<Supplies> suppliesList, Context context) {
        this.suppliesList = suppliesList;
        this.context = context;
    }

    @NonNull
    @Override
    public SuppliesAdapter.SuppliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supplies, parent, false);
        return new SuppliesAdapter.SuppliesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliesAdapter.SuppliesViewHolder holder, int position) {
        Supplies supplies = suppliesList.get(position);
        holder.bind(supplies);
    }

    @Override
    public int getItemCount() {
        return suppliesList.size();
    }
    class SuppliesViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img1,img2,img3,img4;
        private TextInputEditText suppName,suppPrice,suppQuantity;
        private Spinner suppSize,suppStatus,suppCate,suppType;
        private MaterialButton suppEdit,suppDelete;

        public SuppliesViewHolder(@NonNull View itemView) {
            super(itemView);
            suppName = itemView.findViewById(R.id.edt_name);
            suppPrice = itemView.findViewById(R.id.edt_sell_price);
            suppQuantity=itemView.findViewById(R.id.edt_quantity);
            suppEdit = itemView.findViewById(R.id.btnEdit);
            suppDelete = itemView.findViewById(R.id.btnDelete);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
            suppSize=itemView.findViewById(R.id.spinner_size);
            suppStatus=itemView.findViewById(R.id.spinner_status);
            suppCate=itemView.findViewById(R.id.spinner_category);
            suppType=itemView.findViewById(R.id.spinner_type);
            suppEdit.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Supplies supp = suppliesList.get(position);
                Intent intent = new Intent(context, EditSuppliesActivity.class);
                intent.putExtra("supplies_id", supp.getSupplies_id());
               //intent.putExtra("imageUris", supp.getImageUrls());
//                intent.putExtra("category_image", category.getImageUrl());
//                intent.putExtra("category_status", category.getStatus());
                context.startActivity(intent);
            });

            suppDelete.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Supplies supp = suppliesList.get(position);
                deleteSuppliesFromDatabase(supp.getSupplies_id());
                suppliesList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, suppliesList.size());
                Toast.makeText(context, "Đã xóa san pham thành công", Toast.LENGTH_SHORT).show();
            });
        }
        public void bind(Supplies supplies) {
            suppName.setText(supplies.getName() != null ? supplies.getName() : "N/A");
            suppPrice.setText(String.valueOf(supplies.getSell_price()));
            suppQuantity.setText(String.valueOf(supplies.getQuantity()));
            FectchSpinnerSize();
            FectchSpinnerStatus();
            FetchSpinnerType();
            FetchSpinnerCategory();
            List<String> imageUrls = supplies.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // Load the first image
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0)) // Use .get(0) to retrieve the first URL
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.guest)
                        .into(img1);
            }
            // Load the second image if it exists
            if (imageUrls.size() > 1) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(1))
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.guest)
                        .into(img2);
            }
            // Load the third image if it exists
            if (imageUrls.size() > 2) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(2))
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.guest)
                        .into(img3);
            }

            // Load the fourth image if it exists
            if (imageUrls.size() > 3) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(3))
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.guest)
                        .into(img4);
            }

        }

        private void deleteSuppliesFromDatabase(String supplies_Id) {
            FirebaseDatabase.getInstance().getReference("Supplies").child(supplies_Id).removeValue();
        }
        private void FetchSpinnerType()
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Type");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> typeList = new ArrayList<>(); // Danh sách chứa các "type" từ Firebase
                    final Map<String, String> typeMap = new HashMap<>(); // Lưu type_id và type
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Type type = snapshot.getValue(Type.class);
                        if (type != null) {
                            typeList.add(type.getType()); // Thêm "type" vào danh sách
                            typeMap.put(type.getType(), type.getType_id()); // Map "type" với "type_id"
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    suppType.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void FetchSpinnerCategory()
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Category");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> cateList = new ArrayList<>(); // Danh sách chứa các "type" từ Firebase
                    final Map<String, String> cateMap = new HashMap<>(); // Lưu type_id và type
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Category category = snapshot.getValue(Category.class);
                        if (category != null&&category.getStatus()==0) {
                            cateList.add(category.getName());
                            cateMap.put(category.getName(), category.getCategory_id()); // Map "type" với "type_id"
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, cateList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    suppCate.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void FectchSpinnerSize()
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.size_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            suppSize.setAdapter(adapter);
        }
        private void FectchSpinnerStatus()
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.suplies_status, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            suppStatus.setAdapter(adapter);
        }

    }
}
