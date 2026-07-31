package com.project.messmanagement;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_ledger);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        rvTransactions = findViewById(R.id.rv_transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

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