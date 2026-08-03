package com.project.messmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BazarActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;
    private TextView tvTotalAmount, tvPurchasesSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar);

        db = new DatabaseHelper(this);
        container = findViewById(R.id.item_container);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPurchasesSubtitle = findViewById(R.id.tv_purchases_subtitle);

        // 1. Setup Add Button (Floating Action Button)
        findViewById(R.id.btn_add_bazar).setOnClickListener(v -> showBazarDialog(-1, "", 0, ""));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBazarList();
    }

    private void refreshBazarList() {
        if (container == null) return;
        container.removeAllViews();

        double total = 0;
        int count = 0;

        Cursor cursor = db.getAllBazarItems();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                addBazarToUI(id, name, amount, date);
                total += amount;
                count++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Update Top Card
        if (tvTotalAmount != null) tvTotalAmount.setText("৳" + (int) total);
        if (tvPurchasesSubtitle != null) tvPurchasesSubtitle.setText(count + " purchases this month");
    }

    private void showBazarDialog(final int id, String initialName, double initialAmount, String initialDate) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_bazar, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etName = view.findViewById(R.id.etItemName);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etDate = view.findViewById(R.id.etDate);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        if (id != -1) {
            tvTitle.setText("Edit Bazar Item");
            etName.setText(initialName);
            etAmount.setText(String.valueOf(initialAmount));
            etDate.setText(initialDate);
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
            String name = etName.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String date = etDate.getText().toString();

            if (!name.isEmpty() && !amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                if (id == -1) {
                    db.addBazarItem(name, amount, date);
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateBazarItem(id, name, amount, date);
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                }
                refreshBazarList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addBazarToUI(final int id,
                              final String name,
                              final double amount,
                              final String date) {

        // Inflate your custom item layout
        View itemView = getLayoutInflater().inflate(
                R.layout.item_purchase,   // <-- Change this if your XML file has a different name
                container,
                false
        );

        // Find Views
        TextView tvItemName = itemView.findViewById(R.id.tv_item_name);
        TextView tvItemDetails = itemView.findViewById(R.id.tv_item_details);
        TextView tvPrice = itemView.findViewById(R.id.tv_price);

        // Set values
        tvItemName.setText(name);

        // Since your database only stores name, amount and date,
        // we don't have the buyer's name yet.
        tvItemDetails.setText(date);

        // Price
        tvPrice.setText("৳" + (int) amount);

        // Edit on Click
        itemView.setOnClickListener(v ->
                showBazarDialog(id, name, amount, date)
        );

        // Delete on Long Press
        itemView.setOnLongClickListener(v -> {

            new AlertDialog.Builder(BazarActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Delete " + name + "?")
                    .setPositiveButton("Confirm", (dialog, which) -> {

                        db.deleteBazarItem(id);
                        refreshBazarList();

                        Toast.makeText(
                                BazarActivity.this,
                                "Deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        container.addView(itemView);
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
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}
