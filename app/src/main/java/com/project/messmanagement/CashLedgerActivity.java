package com.project.messmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CashLedgerActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;
    private TextView tvBalance, tvIncoming, tvOutgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_ledger);

        db = new DatabaseHelper(this);
        container = findViewById(R.id.transaction_container);
        tvBalance = findViewById(R.id.tv_balance_amount);
        tvIncoming = findViewById(R.id.tv_incoming);
        tvOutgoing = findViewById(R.id.tv_outgoing);

        // 1. Setup Add Button
        findViewById(R.id.fab_add_transaction).setOnClickListener(v -> showCashDialog(-1, "", 0, "IN", ""));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCashList();
    }

    private void refreshCashList() {
        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getAllCashTransactions();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    String date = "N/A";
                    
                    int dateIdx = cursor.getColumnIndex("date");
                    if (dateIdx != -1) {
                        date = cursor.getString(dateIdx);
                    }

                    addCashToUI(id, desc, amount, type, date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Update Totals
        tvBalance.setText("৳" + (int) db.getCashBalance());
        tvIncoming.setText("↗ ৳" + (int) db.getTotalIn() + " in");
        tvOutgoing.setText("↘ ৳" + (int) db.getTotalOut() + " out");
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

        // Spinner Setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"IN", "OUT"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

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

        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view1, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                etDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            String desc = etDesc.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String date = etDate.getText().toString();

            if (!desc.isEmpty() && !amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                if (id == -1) {
                    db.addCashTransaction(desc, amount, type, date);
                    Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateCashTransaction(id, desc, amount, type, date);
                    Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
                }
                refreshCashList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addCashToUI(final int id, final String desc, final double amount, final String type, final String date) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setClickable(true);
        itemLayout.setFocusable(true);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tv = new TextView(this);
        String symbol = type.equals("IN") ? "↗ " : "↘ ";
        tv.setText(symbol + desc + "\n" + date + " | ৳" + (int) amount);
        tv.setTextSize(16);
        tv.setPadding(20, 30, 20, 30);
        tv.setTextColor(type.equals("IN") ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.LTGRAY);

        itemLayout.addView(tv);
        itemLayout.addView(line);

        itemLayout.setOnClickListener(v -> showCashDialog(id, desc, amount, type, date));

        itemLayout.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Transaction")
                    .setMessage("Delete this transaction?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        db.deleteCashTransaction(id);
                        refreshCashList();
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        container.addView(itemLayout);
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
