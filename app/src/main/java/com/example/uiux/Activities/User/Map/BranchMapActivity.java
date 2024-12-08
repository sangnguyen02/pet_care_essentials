package com.example.uiux.Activities.User.Map;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.Admin.Branch.BranchStoreActivity;
import com.example.uiux.Activities.Admin.MainActivityAdmin;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.R;
import com.example.uiux.Utils.VectorToBitmapConverter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraBoundsOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.CoordinateBounds;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.delegates.MapPluginProviderDelegate;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import java.util.ArrayList;
import java.util.List;

public class BranchMapActivity extends AppCompatActivity {
    private MapView mapView;
    private ImageView img_back_branches_map;
    private PointAnnotationManager pointAnnotationManager;
    private ViewAnnotationManager viewAnnotationManager;
    private String[] branchStatusArray; // Mảng chuỗi trạng thái chi nhánh
    private boolean fromBookActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_map);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));

        Intent intent= getIntent();
        fromBookActivity = intent.getBooleanExtra("from_book_activity", false);


        img_back_branches_map = findViewById(R.id.img_back_branches_map);
        img_back_branches_map.setOnClickListener(view -> finish());
        mapView = findViewById(R.id.mapView);
        int vectorResId = R.drawable.mark;
        Bitmap bitmap = VectorToBitmapConverter.convertVectorToBitmap(this, vectorResId);
        branchStatusArray = getResources().getStringArray(R.array.branch_store_status);

        // Thiết lập map và tải style
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
//                mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
//                        .center(Point.fromLngLat(106.688084, 16.054407))
//                        .zoom(5.0)
//                        .pitch(0.0)
//                        .bearing(0.0)
//                        .build()
//                );
                CameraBoundsOptions vietnamBounds = new CameraBoundsOptions.Builder()
                        .bounds(new CoordinateBounds(
                                Point.fromLngLat(102.14441, 8.179066), // Southwest corner of Vietnam
                                Point.fromLngLat(109.464639, 23.393395) // Northeast corner of Vietnam
                        ))
                        .minZoom(5.0) // Set minimum zoom level
                        .maxZoom(15.0) // Set maximum zoom level
                        .build();

                // Áp dụng giới hạn camera để giới hạn khu vực bản đồ chỉ ở Việt Nam
                mapView.getMapboxMap().setBounds(vietnamBounds);
                // Thiết lập vị trí camera ban đầu để hiển thị toàn bộ Việt Nam
                Point vietnamCenter = Point.fromLngLat(108.2068, 16.0471);
                CameraOptions initialCameraOptions = new CameraOptions.Builder()
                        .center(vietnamCenter)
                        .zoom(5.0)
                        .bearing(0.0)
                        .pitch(0.0)
                        .build();
                mapView.getMapboxMap().setCamera(initialCameraOptions);

                AnnotationPlugin annotationAPI = AnnotationPluginImplKt.getAnnotations((MapPluginProviderDelegate) mapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationAPI, mapView);
                viewAnnotationManager = mapView.getViewAnnotationManager(); // Khởi tạo viewAnnotationManager

                // Gọi hàm để lấy các điểm từ Firebase
                fetchBranchStores(bitmap);
            }
        });
    }

    private void fetchBranchStores(Bitmap bitmap) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BranchStore branchStore = snapshot.getValue(BranchStore.class);
                    if (branchStore != null) {
                        Point point = Point.fromLngLat(branchStore.getLongtitude(), branchStore.getLatitude());
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                .withPoint(point)
                                .withIconImage(bitmap);

                        // Tạo annotation cho mỗi điểm
                        PointAnnotation pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);

                        // Thêm ClickListener cho annotation để hiển thị custom annotation
                        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
                            @Override
                            public boolean onAnnotationClick(@NonNull PointAnnotation clickedAnnotation) {
                                if (clickedAnnotation == pointAnnotation) {
                                    showCustomAnnotation(branchStore, clickedAnnotation.getPoint());
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi đọc dữ liệu
                Log.e("BranchMapActivity", "Lỗi khi lấy dữ liệu từ Firebase", databaseError.toException());
            }
        });
    }
    private void showCustomAnnotation(BranchStore branchStore, Point point) {
        // Xóa bất kỳ ViewAnnotation nào trước đó nếu cần
        viewAnnotationManager.removeAllViewAnnotations();

        // Tạo ViewAnnotation với layout tùy chỉnh
        ViewAnnotationOptions options = new ViewAnnotationOptions.Builder()
                .geometry(point)
                .build();

        View viewAnnotation = viewAnnotationManager.addViewAnnotation(R.layout.annotation_map, options);

        // Thiết lập dữ liệu cho layout tùy chỉnh
        TextView branchNameTextView = viewAnnotation.findViewById(R.id.branch_name);
        TextView branchStatusTextView = viewAnnotation.findViewById(R.id.branch_status);
        MaterialButton booking =viewAnnotation.findViewById(R.id.book);
        branchNameTextView.setText(branchStore.getBranch_name());
        // Lấy trạng thái từ mảng chuỗi và thiết lập vào TextView
        int statusIndex = branchStore.getStatus();
        if (statusIndex >= 0 && statusIndex < branchStatusArray.length) {
            branchStatusTextView.setText(branchStatusArray[statusIndex]);
        } else {
            branchStatusTextView.setText("Không rõ trạng thái");
        }
        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromBookActivity) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selected_branch", branchStore.getBranch_Store_id());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Intent intent=new Intent(BranchMapActivity.this, BranchStoreActivity.class);
                    startActivity(intent);
                }

            }
        });

        viewAnnotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BranchMapActivity.this, "View Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}