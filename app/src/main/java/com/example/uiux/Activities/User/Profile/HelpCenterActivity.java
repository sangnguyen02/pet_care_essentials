package com.example.uiux.Activities.User.Profile;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Adapters.HelpCenterAdapter;
import com.example.uiux.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpCenterActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private HelpCenterAdapter helpCenterAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ImageView imgv_back_help_center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_center);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        imgv_back_help_center = findViewById(R.id.imgv_back_help_center);
        imgv_back_help_center.setOnClickListener(view -> finish());
        expandableListView = findViewById(R.id.expandableListView);
        listDataChild = getFAQData();
        listDataHeader = new ArrayList<>(listDataChild.keySet());

        helpCenterAdapter = new HelpCenterAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(helpCenterAdapter);

    }

    private HashMap<String, List<String>> getFAQData() {
        HashMap<String, List<String>> faqData = new HashMap<>();

        String[] questions = {
                getString(R.string.question_1),
                getString(R.string.question_2),
                getString(R.string.question_3),
                getString(R.string.question_4),
                getString(R.string.question_5),
                getString(R.string.question_6),
                getString(R.string.question_7),
                getString(R.string.question_8),
                getString(R.string.question_9),
                getString(R.string.question_10)
        };

        String[] answers = {
                getString(R.string.answer_1),
                getString(R.string.answer_2),
                getString(R.string.answer_3),
                getString(R.string.answer_4),
                getString(R.string.answer_5),
                getString(R.string.answer_6),
                getString(R.string.answer_7),
                getString(R.string.answer_8),
                getString(R.string.answer_9),
                getString(R.string.answer_10)
        };

        for (int i = 0; i < questions.length; i++) {
            List<String> answerList = new ArrayList<>();
            answerList.add(answers[i]);
            faqData.put(questions[i], answerList);
        }

        return faqData;
    }
}