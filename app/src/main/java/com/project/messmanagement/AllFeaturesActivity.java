package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AllFeaturesActivity extends AppCompatActivity {

    private RecyclerView rvStats;
    private StatAdapter statAdapter;
    private List<StatItem> statList;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_features);

        // The feature grid is now statically defined in XML for perfect visual fidelity
        // in the layout editor. Individual click listeners can be added here.

        btn_home = findViewById(R.id.btn_home_layout); // pending
        btn_member = findViewById(R.id.btn_member_layout); //pending

        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);

        btn_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AllFeaturesActivity.this, MealRoutineActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_bazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AllFeaturesActivity.this, BazarActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AllFeaturesActivity.this, CashLedgerActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setupStatsList();
    }

    private void setupStatsList() {
        rvStats = findViewById(R.id.rv_quick_stats);
        rvStats.setLayoutManager(new LinearLayoutManager(this));
        
        statList = new ArrayList<>();
        statList.add(new StatItem("Bua salary due", "৳4,000", true));
        statList.add(new StatItem("Outstanding loans", "৳3,000", false));
        statList.add(new StatItem("Pending room requests", "2 items", false));
        statList.add(new StatItem("Unread complaints", "2 new", true));

        statAdapter = new StatAdapter(statList);
        rvStats.setAdapter(statAdapter);
    }
}