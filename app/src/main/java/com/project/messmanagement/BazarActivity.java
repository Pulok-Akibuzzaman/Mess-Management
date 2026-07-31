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

public class BazarActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private PurchaseAdapter adapter;
    private List<PurchaseItem> historyList;
    private AppDatabase database;
    private SessionManager sessionManager;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        rvHistory = findViewById(R.id.rv_purchase_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        btn_home = findViewById(R.id.btn_home_layout);
        btn_member = findViewById(R.id.btn_member_layout); //pending

        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BazarActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BazarActivity.this, MealRoutineActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_bazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BazarActivity.this, CashLedgerActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BazarActivity.this, AllFeaturesActivity.class);
                startActivity(i);
                finish();
            }
        });

        loadHistoryData();
    }

    private void loadHistoryData() {
        new Thread(() -> {
            int userId = sessionManager.getUserId();
            List<Purchase> purchases = database.purchaseDao().getPurchasesByUser(userId);

            historyList = new ArrayList<>();
            for (Purchase p : purchases) {
                historyList.add(new PurchaseItem(p.getName(), p.getDate(), p.getPrice()));
            }

            runOnUiThread(() -> {
                adapter = new PurchaseAdapter(historyList);
                rvHistory.setAdapter(adapter);
            });
        }).start();
    }
}