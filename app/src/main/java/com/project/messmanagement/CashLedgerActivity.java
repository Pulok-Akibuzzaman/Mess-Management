package com.project.messmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CashLedgerActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private CashLedgerAdapter adapter;
    private final List<CashTransaction> transactionList = new ArrayList<>();
    private TextView tvBalance, tvIncoming, tvOutgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_ledger);

        db = new DatabaseHelper(this);

        RecyclerView rvTransactions = findViewById(R.id.transaction_container);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CashLedgerAdapter(transactionList,
                (position, transaction) -> showCashDialog(
                        transaction.id, transaction.description, transaction.amount,
                        transaction.type, transaction.date),
                (position, transaction) -> confirmDeleteTransaction(transaction));
        rvTransactions.setAdapter(adapter);

        tvBalance = findViewById(R.id.tv_balance_amount);
        tvIncoming = findViewById(R.id.tv_incoming);
        tvOutgoing = findViewById(R.id.tv_outgoing);

        findViewById(R.id.fab_add_transaction).setOnClickListener(v -> showCashDialog(-1, "", 0, "IN", ""));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCashList();
    }

    private void refreshCashList() {
        transactionList.clear();

        Cursor cursor = db.getAllCashTransactions();
        if (cursor != null) {
            int idxId = cursor.getColumnIndexOrThrow("id");
            int idxDesc = cursor.getColumnIndexOrThrow("description");
            int idxAmount = cursor.getColumnIndexOrThrow("amount");
            int idxType = cursor.getColumnIndexOrThrow("type");
            int idxDate = cursor.getColumnIndex("date");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idxId);
                String desc = cursor.getString(idxDesc);
                double amount = cursor.getDouble(idxAmount);
                String type = cursor.getString(idxType);
                String date = idxDate != -1 ? cursor.getString(idxDate) : "N/A";

                transactionList.add(new CashTransaction(id, desc, amount, type, date));
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();

        tvBalance.setText("৳" + (int) db.getCashBalance());
        tvIncoming.setText("↗ ৳" + (int) db.getTotalIn() + " in");
        tvOutgoing.setText("↘ ৳" + (int) db.getTotalOut() + " out");
    }

    /** Shown on long-press of a transaction card. */
    private void confirmDeleteTransaction(CashTransaction transaction) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteCashTransaction(transaction.id);
                    refreshCashList();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCashDialog(final int id, String initialDesc, double initialAmount, String initialType, String initialDate) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_cash, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        final Spinner spinnerType = view.findViewById(R.id.spinnerType);
        final EditText etDesc = view.findViewById(R.id.etDescription);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etDate = view.findViewById(R.id.etDate);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"IN", "OUT"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        if (id != -1) {
            tvTitle.setText("Edit Transaction");
            etDesc.setText(initialDesc);
            etAmount.setText(String.valueOf(initialAmount));
            etDate.setText(initialDate);
            spinnerType.setSelection(initialType.equals("IN") ? 0 : 1);
        } else {
            etDate.setText(sdf.format(calendar.getTime()));
        }

        etDate.setOnClickListener(v -> new DatePickerDialog(this, (view1, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            etDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        btnSave.setOnClickListener(v -> {
            String desc = etDesc.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String date = etDate.getText().toString();

            if (desc.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id == -1) {
                db.addCashTransaction(desc, amount, type, date);
                Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show();
            } else {
                db.updateCashTransaction(id, desc, amount, type, date);
                Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
            }
            refreshCashList();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}