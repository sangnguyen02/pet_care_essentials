package com.example.uiux.Activities.Admin.Branch;

import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.uiux.Model.BranchStore;
import com.example.uiux.R;
import com.example.uiux.Utils.VectorToBitmapConverter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi;
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer;
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement;
import com.mapbox.navigation.ui.voice.model.SpeechError;
import com.mapbox.navigation.ui.voice.model.SpeechValue;
import com.mapbox.navigation.ui.voice.model.SpeechVolume;
import com.mapbox.navigation.ui.voice.view.MapboxSoundButton;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;


//public class TestMapActivity extends AppCompatActivity {
//    MapView mapView;
//    FloatingActionButton focusLocationBtn;
//    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
//    private boolean focusLocation = true;
//    private MapboxNavigation mapboxNavigation;
//    private PlaceAutocomplete placeAutocomplete;
//    private SearchResultsView searchResultsView;
//    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
//    private TextInputEditText searchET;
//    private boolean ignoreNextQueryUpdate = false;
//
//    private final LocationObserver locationObserver = new LocationObserver() {
//        @Override
//        public void onNewRawLocation(@NonNull Location location) {}
//
//        @Override
//        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
//            Location location = locationMatcherResult.getEnhancedLocation();
//            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
//            if (focusLocation) {
//                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
//            }
//        }
//    };
//
//    private void updateCamera(Point point, Double bearing) {
//        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
//        CameraOptions cameraOptions = new CameraOptions.Builder()
//                .center(point).zoom(18.0).bearing(bearing).pitch(45.0).build();
//        getCamera(mapView).easeTo(cameraOptions, animationOptions);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_map);
//
//        mapView = findViewById(R.id.mapView);
//        focusLocationBtn = findViewById(R.id.focusLocation);
//        int vectorResId = R.drawable.here; // Thay thế bằng id của hình ảnh vector của bạn
//        Bitmap bitmap = VectorToBitmapConverter.convertVectorToBitmap(this, vectorResId);
//
//        NavigationOptions navigationOptions = new NavigationOptions.Builder(this)
//                .accessToken(getString(R.string.mapbox_access_token)).build();
//        MapboxNavigationApp.setup(navigationOptions);
//        mapboxNavigation = new MapboxNavigation(navigationOptions);
//        mapboxNavigation.registerLocationObserver(locationObserver);
//
//        // Autocomplete Search
//        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
//        searchET = findViewById(R.id.searchET);
//        searchResultsView = findViewById(R.id.search_results_view);
//        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
//
//        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(
//                searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(this)
//        );
//
//        searchET.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (ignoreNextQueryUpdate) {
//                    ignoreNextQueryUpdate = false;
//                } else {
//                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
//                        @NonNull @Override public CoroutineContext getContext() { return EmptyCoroutineContext.INSTANCE; }
//
//                        @Override
//                        public void resumeWith(@NonNull Object o) {
//                            runOnUiThread(() -> searchResultsView.setVisibility(View.VISIBLE));
//                        }
//                    });
//                }
//            }
//
//            @Override public void afterTextChanged(Editable editable) {}
//        });
//
//        // Map and Markers
//        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE, style -> {
//           // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.here);
//            AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
//            PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
//
//            // Map click to add marker
//            addOnMapClickListener(mapView.getMapboxMap(), point -> {
//                pointAnnotationManager.deleteAll();
//                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                        .withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap).withPoint(point);
//                pointAnnotationManager.create(pointAnnotationOptions);
//                return true;
//            });
//
//            // Search Result Selection
//            placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
//                @Override
//                public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {}
//
//                @Override
//                public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion suggestion) {
//                    ignoreNextQueryUpdate = true;
//                    focusLocation = false;
//                    searchET.setText(suggestion.getName());
//                    searchResultsView.setVisibility(View.GONE);
//
//                    pointAnnotationManager.deleteAll();
//                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                            .withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap).withPoint(suggestion.getCoordinate());
//                    pointAnnotationManager.create(pointAnnotationOptions);
//                    updateCamera(suggestion.getCoordinate(), 0.0);
//                }
//
//                @Override public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion suggestion) {}
//                @Override public void onError(@NonNull Exception e) {}
//            });
//        });
//
//        focusLocationBtn.setOnClickListener(view -> {
//            focusLocation = true;
//            focusLocationBtn.hide();
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapboxNavigation.unregisterLocationObserver(locationObserver);
//        mapboxNavigation.onDestroy();
//    }
//}
public class TestMapActivity extends AppCompatActivity {
    MapView mapView;
    FloatingActionButton focusLocationBtn;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private boolean focusLocation = true;
    private MapboxNavigation mapboxNavigation;
    private PlaceAutocomplete placeAutocomplete;
    private SearchResultsView searchResultsView;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    private DatabaseReference databaseReference;
    private Spinner statusSpinner;
    private TextInputEditText branchName;
    private MaterialButton saveBtn;
    BranchStore branchStore;
    private Point selectedPoint;

    // Location observer
    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {}

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(point).zoom(12.0).bearing(bearing).pitch(45.0).build();
        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);

        mapView = findViewById(R.id.mapView);
        statusSpinner=findViewById(R.id.statusSpinner);
        branchName=findViewById(R.id.edt_branch_name);
        saveBtn=findViewById(R.id.save);

        focusLocationBtn = findViewById(R.id.focusLocation);
        int vectorResId = R.drawable.here; // Replace with the ID of your vector image resource
        Bitmap bitmap = VectorToBitmapConverter.convertVectorToBitmap(this, vectorResId);

        FectchSpinnerStatus();
        branchStore=new BranchStore();
        databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

        NavigationOptions navigationOptions = new NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token)).build();
        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);
        mapboxNavigation.registerLocationObserver(locationObserver);

        // Autocomplete search setup
        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchET = findViewById(R.id.searchET);
        searchResultsView = findViewById(R.id.search_results_view);
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));

        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(
                searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(this)
        );

        searchET.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull @Override public CoroutineContext getContext() { return EmptyCoroutineContext.INSTANCE; }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            runOnUiThread(() -> searchResultsView.setVisibility(View.VISIBLE));
                        }
                    });
                }
            }

            @Override public void afterTextChanged(Editable editable) {}
        });

        // Load the map style and set initial position
        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE, style -> {
            // Center the map on Vietnam initially
            Point vietnamCenter = Point.fromLngLat(108.2068, 16.0471); // Da Nang area
            updateCamera(vietnamCenter, 0.0);

            AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
            PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

            // Map click to add marker
            addOnMapClickListener(mapView.getMapboxMap(), point -> {
                pointAnnotationManager.deleteAll();
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap).withPoint(point);
                pointAnnotationManager.create(pointAnnotationOptions);
                selectedPoint = point; // Set the clicked point as the branch location
                return true;
            });

            // Search result selection
            placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
                @Override
                public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {}

                @Override
                public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion suggestion) {
                    ignoreNextQueryUpdate = true;
                    focusLocation = false;
                    searchET.setText(suggestion.getName());
                    searchResultsView.setVisibility(View.GONE);

                    pointAnnotationManager.deleteAll();
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap).withPoint(suggestion.getCoordinate());
                    pointAnnotationManager.create(pointAnnotationOptions);
                    selectedPoint=suggestion.getCoordinate();
                    updateCamera(suggestion.getCoordinate(), 0.0);
                }

                @Override public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion suggestion) {}
                @Override public void onError(@NonNull Exception e) {}
            });
        });

        focusLocationBtn.setOnClickListener(view -> {
            focusLocation = true;
            focusLocationBtn.hide();
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPoint == null || searchET.getText().toString().isEmpty()) {
                    Toast.makeText(TestMapActivity.this, "Select a location and enter a branch name.", Toast.LENGTH_SHORT).show();;
                }
                else {
                    uploadBranchStoreToFirebase();

                }
            }
        });
    }

    private void uploadBranchStoreToFirebase()
    {
        String id = databaseReference.push().getKey();
        String name=branchName.getText().toString();
        String address=searchET.getText().toString();
        double longtitude= selectedPoint.longitude();
        double latitude=selectedPoint.latitude();
        if (id != null) {
            branchStore.setBranch_Store_id(id);
            branchStore.setBranch_name(name);
            branchStore.setAddress_details(address);
            branchStore.setLongtitude(longtitude);
            branchStore.setLatitude(latitude);
            databaseReference.child(id).setValue(branchStore)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(TestMapActivity.this, "BranchStore successfully saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TestMapActivity.this, "Failed to save BranchStore!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapboxNavigation.unregisterLocationObserver(locationObserver);
        mapboxNavigation.onDestroy();
    }
    private void FectchSpinnerStatus() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(TestMapActivity.this,
                R.array.branch_store_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }
}

