package com.project.messmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

public class BuaManagementActivity extends AppCompatActivity {

    private Button btnProfile, btnSalary, btnSchedule;
    private FrameLayout tabContent;
    private DatabaseHelper db;

    private TextView tvNameHeader, tvPhoneHeader, tvAddressHeader, tvSalaryStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bua_management);

        db = new DatabaseHelper(this);
        initViews();
        loadBuaData();
        setupNavigation();

        btnProfile.setOnClickListener(v -> { setActiveTab(btnProfile); showProfile(); });
        btnSalary.setOnClickListener(v -> { setActiveTab(btnSalary); showSalary(); });
        btnSchedule.setOnClickListener(v -> { setActiveTab(btnSchedule); showSchedule(); });

        findViewById(R.id.btnEditBua).setOnClickListener(v -> showEditBuaDialog());
    }

    private void initViews() {
        btnProfile = findViewById(R.id.btnTabProfile);
        btnSalary = findViewById(R.id.btnTabSalary);
        btnSchedule = findViewById(R.id.btnTabSchedule);
        tabContent = findViewById(R.id.tabContent);

        tvNameHeader = findViewById(R.id.tvBuaNameHeader);
        tvPhoneHeader = findViewById(R.id.tvBuaPhoneHeader);
        tvAddressHeader = findViewById(R.id.tvBuaAddressHeader);
        tvSalaryStat = findViewById(R.id.tvBuaSalaryStat);

        showProfile();
    }

    private void loadBuaData() {
        Cursor cursor = db.getBuaProfile();
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            double salary = cursor.getDouble(cursor.getColumnIndexOrThrow("salary"));

            tvNameHeader.setText(name);
            tvPhoneHeader.setText(phone);
            tvAddressHeader.setText(address);
            tvSalaryStat.setText(String.format(Locale.US, "৳%.0f", salary));
            cursor.close();
        }
    }

    private void showEditBuaDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_bua, null);
        dialog.setContentView(view);

        final EditText etName = view.findViewById(R.id.etBuaName);
        final EditText etPhone = view.findViewById(R.id.etBuaPhone);
        final EditText etAddress = view.findViewById(R.id.etBuaAddress);
        final EditText etSalary = view.findViewById(R.id.etBuaSalary);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        // Pre-fill
        Cursor cursor = db.getBuaProfile();
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            etSalary.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("salary"))));
            cursor.close();
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String salaryStr = etSalary.getText().toString().trim();

            if (!name.isEmpty() && !salaryStr.isEmpty()) {
                db.updateBuaProfile(name, phone, address, Double.parseDouble(salaryStr));
                loadBuaData();
                showProfile();
                dialog.dismiss();
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setActiveTab(Button active) {
        for (Button btn : new Button[]{btnProfile, btnSalary, btnSchedule}) {
            btn.setBackgroundResource(android.R.color.transparent);
            btn.setTextColor(Color.GRAY);
        }
        active.setBackgroundResource(R.drawable.bg_tab_active);
        active.setTextColor(Color.WHITE);
    }

    private void showProfile() {
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_profile, tabContent, false);
        
        Cursor cursor = db.getBuaProfile();
        if (cursor != null && cursor.moveToFirst()) {
            setRow(view, R.id.rowFullName,  "Full Name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
            setRow(view, R.id.rowPhone,     "Phone",     cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            setRow(view, R.id.rowAddress,   "Address",   cursor.getString(cursor.getColumnIndexOrThrow("address")));
            setRow(view, R.id.rowJoinDate,  "Join Date", cursor.getString(cursor.getColumnIndexOrThrow("join_date")));
            setRow(view, R.id.rowNationalId,"National ID","19XXXXXXXXX");
            setRow(view, R.id.rowEmergency, "Emergency", "018XXXXXXXX");
            cursor.close();
        }
        tabContent.addView(view);
    }

    private void setRow(View parent, int rowId, String label, String value) {
        View row = parent.findViewById(rowId);
        if (row != null) {
            ((TextView) row.findViewById(R.id.tvLabel)).setText(label);
            ((TextView) row.findViewById(R.id.tvValue)).setText(value);
        }
    }

    private void showSalary() {
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_salary, tabContent, false);
        tabContent.addView(view);
    }

    private void showSchedule() {
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_schedule, tabContent, false);
        tabContent.addView(view);
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> { startActivity(new Intent(this, MemberActivity.class)); finish(); });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> { startActivity(new Intent(this, MealRoutineActivity.class)); finish(); });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> { startActivity(new Intent(this, BazarActivity.class)); finish(); });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> { startActivity(new Intent(this, CashLedgerActivity.class)); finish(); });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> { startActivity(new Intent(this, AllFeaturesActivity.class)); finish(); });
    }
}
