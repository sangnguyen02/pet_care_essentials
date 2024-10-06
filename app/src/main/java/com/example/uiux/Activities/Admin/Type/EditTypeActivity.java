package com.example.uiux.Activities.Admin.Type;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTypeActivity extends AppCompatActivity {

    private ImageView img_back_edit_type;
    private MaterialButton saveType;
    private TextInputEditText typeName;
    private DatabaseReference typeRef;
    private String type_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_edit_type);
        img_back_edit_type = findViewById(R.id.img_back_edit_type);
        img_back_edit_type.setOnClickListener(view -> finish());
        saveType=findViewById(R.id.btnSaveType);
        typeName=findViewById(R.id.edt_type);
        type_id = getIntent().getStringExtra("type_id");
        typeRef = FirebaseDatabase.getInstance().getReference("Type").child(type_id);
        loadTypeData();
        saveType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTypeData();
            }
        });

    }

    private void saveTypeData() {
        if(typeName.getText().toString()!=null)
        {
            String updatedName = typeName.getText().toString().trim();
            typeRef.child("type").setValue(updatedName);
            Toast.makeText(EditTypeActivity.this, "Type updated successfully", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(EditTypeActivity.this, "Type updated failed", Toast.LENGTH_SHORT).show();
        }




    }

    private void loadTypeData() {
        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Type type = snapshot.getValue(Type.class);
                    typeName.setText(type.getType());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditTypeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}