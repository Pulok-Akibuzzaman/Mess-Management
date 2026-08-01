package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NoticesActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        container = findViewById(R.id.notice_container);

        // Add sample notices
        addNotice("Meeting Tonight", "Meeting at 9 PM to discuss meal rates.", "2026-08-01");
        addNotice("Electricity Bill", "Please pay your share by Friday.", "2026-07-30");

        findViewById(R.id.btnAddNotice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NoticesActivity.this, "Add Notice clicked", Toast.LENGTH_SHORT).show();
            }
        });

        setupNavigation();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }

    private void addNotice(String title, String content, String date) {
        if (container == null) return;

        TextView tv = new TextView(this);
        tv.setText(title + "\n" + content + "\n" + date);
        tv.setTextSize(16);
        tv.setPadding(20, 30, 20, 30);
        tv.setTextColor(android.graphics.Color.BLACK);

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(android.graphics.Color.LTGRAY);

        container.addView(tv);
        container.addView(line);
    }
}
