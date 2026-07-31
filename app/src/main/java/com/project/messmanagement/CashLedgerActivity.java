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

public class CashLedgerActivity extends AppCompatActivity {

    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private List<TransactionItem> transactionList;
    private AppDatabase database;
    private SessionManager sessionManager;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_ledger);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        rvTransactions = findViewById(R.id.rv_transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));


        btn_home = findViewById(R.id.btn_home_layout); // pending
        btn_member = findViewById(R.id.btn_member_layout); //pending

        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);

        btn_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CashLedgerActivity.this, MealRoutineActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_bazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CashLedgerActivity.this, BazarActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CashLedgerActivity.this, AllFeaturesActivity.class);
                startActivity(i);
                finish();
            }
        });

        loadTransactionData();
    }

    private void loadTransactionData() {
        new Thread(() -> {
            int userId = sessionManager.getUserId();
            List<Transaction> transactions = database.transactionDao().getTransactionsByUser(userId);

            transactionList = new ArrayList<>();
            for (Transaction t : transactions) {
                transactionList.add(new TransactionItem(t.getDescription(), t.getDate(), t.getAmount(), t.isIncoming()));
            }

            runOnUiThread(() -> {
                adapter = new TransactionAdapter(transactionList);
                rvTransactions.setAdapter(adapter);
            });
        }).start();
    }
}