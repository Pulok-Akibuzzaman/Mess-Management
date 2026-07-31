package com.project.messmanagement;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        rvHistory = findViewById(R.id.rv_purchase_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

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