package com.example.uiux.Activities;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.R;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initWidget();
        searchViewConfig();

    }

    void initWidget() {
        searchView = findViewById(R.id.search_view);

    }

    void searchViewConfig() {
        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(searchTextId);
        searchText.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

}