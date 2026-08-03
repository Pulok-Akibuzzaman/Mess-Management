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

public class LoanActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices); // Reusing layout for list

        db = new DatabaseHelper(this);
        container = findViewById(R.id.notice_container);
        
        TextView tvTitle = findViewById(R.id.tvActivityTitle);
        if (tvTitle != null) tvTitle.setText("Loans");
        
        // 1. Setup Add Button
        findViewById(R.id.btnAddNotice).setOnClickListener(v -> showLoanDialog(-1, "", 0, "Pending", ""));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLoanList();
    }

    private void refreshLoanList() {
        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getAllLoans();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String lender = cursor.getString(cursor.getColumnIndexOrThrow("lender"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                addLoanToUI(id, lender, amount, status, date);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showLoanDialog(final int id, String initialLender, double initialAmount, String initialStatus, String initialDate) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_loan, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etLender = view.findViewById(R.id.etLenderName);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etDate = view.findViewById(R.id.etDate);
        final Spinner spinnerStatus = view.findViewById(R.id.spinnerLoanStatus);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        // Status Spinner Setup
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Pending", "Urgent", "Paid"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        if (id != -1) {
            tvTitle.setText("Edit Loan");
            etLender.setText(initialLender);
            etAmount.setText(String.valueOf(initialAmount));
            etDate.setText(initialDate);
            spinnerStatus.setSelection(statusAdapter.getPosition(initialStatus));
            btnSave.setText("Update Loan");
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
            String lender = etLender.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();
            String date = etDate.getText().toString();

            if (!lender.isEmpty() && !amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                if (id == -1) {
                    db.addLoan(lender, amount, status, date);
                    Toast.makeText(this, "Loan added", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateLoan(id, lender, amount, status, date);
                    Toast.makeText(this, "Loan updated", Toast.LENGTH_SHORT).show();
                }
                refreshLoanList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addLoanToUI(final int id, final String lender, final double amount, final String status, final String date) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setClickable(true);
        itemLayout.setFocusable(true);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tv = new TextView(this);
        tv.setText(lender + "\n" + date + " | ৳" + (int) amount + "\nStatus: " + status);
        tv.setTextSize(16);
        tv.setPadding(20, 30, 20, 30);
        
        if (status.equalsIgnoreCase("Urgent")) tv.setTextColor(Color.RED);
        else if (status.equalsIgnoreCase("Paid")) tv.setTextColor(Color.parseColor("#4CAF50"));
        else tv.setTextColor(Color.BLACK);

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.LTGRAY);

        itemLayout.addView(tv);
        itemLayout.addView(line);

        // Edit on Click
        itemLayout.setOnClickListener(v -> showLoanDialog(id, lender, amount, status, date));

        // Long Press to Delete
        itemLayout.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Loan")
                    .setMessage("Delete this loan entry?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        db.deleteLoan(id);
                        refreshLoanList();
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
