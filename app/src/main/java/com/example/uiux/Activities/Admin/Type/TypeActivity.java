package com.example.uiux.Activities.Admin.Type;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.Admin.Category.CategoryActivity;
import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TypeActivity extends AppCompatActivity {
    private ImageView img_back_new_type;
    private MaterialButton addType;
    private TextInputEditText typeName;
    private DatabaseReference typeDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_type);
        img_back_new_type = findViewById(R.id.img_back_new_type);
        img_back_new_type.setOnClickListener(view -> finish());
        addType=findViewById(R.id.btnAddType);
        typeName=findViewById(R.id.edt_type);
        typeDatabase = FirebaseDatabase.getInstance().getReference("Type");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        addType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeName.getText() != null ) {
                    AddTypeToFireBase();
                } else {
                    Toast.makeText(TypeActivity.this, "Please select enter a type name", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void AddTypeToFireBase() {
        String typeId = typeDatabase.push().getKey();
        String type=typeName.getText().toString().trim();
        Type typeModel = new Type(typeId,type);
        typeDatabase.child(typeId).setValue(typeModel).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(TypeActivity.this, "Type added successfully", Toast.LENGTH_SHORT).show();
                // Reset giao diện sau khi thêm thành công
                typeName.setText("");

            } else {
                Toast.makeText(TypeActivity.this, "Failed to add Type", Toast.LENGTH_SHORT).show();
            }
        });


    }
}