package com.example.uiux.Activities.User.Map;

import static android.provider.MediaStore.Images.Media.getBitmap;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uiux.R;
import com.example.uiux.Utils.VectorToBitmapConverter;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.delegates.MapPluginProviderDelegate;

import java.util.ArrayList;
import java.util.List;

public class BranchMapActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_map);

        mapView = findViewById(R.id.mapView);
        int vectorResId = R.drawable.here; // Thay thế bằng id của hình ảnh vector của bạn
        Bitmap bitmap = VectorToBitmapConverter.convertVectorToBitmap(this, vectorResId);
        // Danh sách các tọa độ cho các điểm đánh dấu
        List<Point> points = new ArrayList<>();
        points.add(Point.fromLngLat(106.6970, 10.7758)); // Điểm 1
        points.add(Point.fromLngLat(106.7000, 10.7800)); // Điểm 2
        points.add(Point.fromLngLat(106.7050, 10.7850)); // Điểm 3

// Sử dụng Bitmap theo nhu cầu của bạn


        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Đặt camera ở vị trí trung tâm của Việt Nam
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                        .center(Point.fromLngLat(106.688084, 16.054407)) // Tọa độ trung tâm Việt Nam
                        .zoom(5.0) // Thu phóng phù hợp
                        .pitch(0.0) // Loại bỏ độ nghiêng
                        .bearing(0.0) // Hướng về phía bắc
                        .build()
                );

                AnnotationPlugin annotationAPI = AnnotationPluginImplKt.getAnnotations((MapPluginProviderDelegate)mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationAPI,mapView);
//                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                        .withPoint(com.mapbox.geojson.Point.fromLngLat(106.6970, 10.7758))
//                        .withIconImage(bitmap);
//                pointAnnotationManager.create(pointAnnotationOptions);
                for (Point point : points) {
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(bitmap); // Sử dụng Bitmap ở đây
                    pointAnnotationManager.create(pointAnnotationOptions);
                }



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
