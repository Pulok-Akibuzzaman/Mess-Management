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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BuaManagementActivity extends AppCompatActivity {

    private Button btnProfile, btnSalary, btnSchedule;
    private FrameLayout tabContent;
    private DatabaseHelper db;

    private TextView tvNameHeader, tvPhoneHeader, tvAddressHeader, tvSalaryStat;
    private TextView tvServiceStat, tvJoinedStat;

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
        tvServiceStat = findViewById(R.id.tvBuaServiceStat);
        tvJoinedStat = findViewById(R.id.tvBuaJoinedStat);

        showProfile();
    }

    private void loadBuaData() {
        Cursor cursor = db.getBuaProfile();
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            double salary = cursor.getDouble(cursor.getColumnIndexOrThrow("salary"));
            String joinDateStr = cursor.getString(cursor.getColumnIndexOrThrow("join_date"));

            tvNameHeader.setText(name);
            tvPhoneHeader.setText(phone);
            tvAddressHeader.setText(address);
            tvSalaryStat.setText(String.format(Locale.US, "৳%.0f", salary));
            
            // Update stats cards
            tvServiceStat.setText(calculateShortTenure(joinDateStr));
            tvJoinedStat.setText(formatShortJoinDate(joinDateStr));
            
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
        final EditText etJoinDate = view.findViewById(R.id.etBuaJoinDate);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        // Pre-fill
        Cursor cursor = db.getBuaProfile();
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            etSalary.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("salary"))));
            etJoinDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("join_date")));
            cursor.close();
        }

        etJoinDate.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            new android.app.DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                java.util.Calendar picked = java.util.Calendar.getInstance();
                picked.set(year, month, dayOfMonth);
                etJoinDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(picked.getTime()));
            }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String salaryStr = etSalary.getText().toString().trim();
            String joinDate = etJoinDate.getText().toString().trim();

            if (!name.isEmpty() && !salaryStr.isEmpty() && !joinDate.isEmpty()) {
                db.updateBuaProfile(name, phone, address, Double.parseDouble(salaryStr), joinDate);
                dialog.dismiss();
                
                loadBuaData();
                setActiveTab(btnProfile); 
                showProfile();
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
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String joinDateStr = cursor.getString(cursor.getColumnIndexOrThrow("join_date"));
            
            setRow(view, R.id.rowFullName,  "Full Name", name);
            setRow(view, R.id.rowPhone,     "Phone",     cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            setRow(view, R.id.rowAddress,   "Address",   cursor.getString(cursor.getColumnIndexOrThrow("address")));
            setRow(view, R.id.rowJoinDate,  "Join Date", joinDateStr);
            
            // Calculate Tenure
            String tenure = calculateTenure(joinDateStr);
            setRow(view, R.id.rowTenure,    "Service Time", tenure);
            
            setRow(view, R.id.rowEmergency, "Emergency", "018XXXXXXXX");
            cursor.close();
        }
        tabContent.addView(view);
    }

    private String calculateShortTenure(String joinDateStr) {
        if (joinDateStr == null || joinDateStr.isEmpty() || joinDateStr.equalsIgnoreCase("N/A")) return "0y 0m";
        try {
            java.util.Date joinDate = null;
            String[] formats = {"dd MMM yyyy", "MMMM yyyy", "MMM yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};
            for (String f : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.US);
                    joinDate = sdf.parse(joinDateStr);
                    if (joinDate != null) break;
                } catch (Exception ignored) {}
            }
            if (joinDate == null) return "0y 0m";

            java.util.Calendar join = java.util.Calendar.getInstance();
            join.setTime(joinDate);
            java.util.Calendar now = java.util.Calendar.getInstance();

            int diffMonths = (now.get(java.util.Calendar.YEAR) - join.get(java.util.Calendar.YEAR)) * 12 + 
                             (now.get(java.util.Calendar.MONTH) - join.get(java.util.Calendar.MONTH));
            
            if (now.get(java.util.Calendar.DAY_OF_MONTH) < join.get(java.util.Calendar.DAY_OF_MONTH)) {
                diffMonths--;
            }

            if (diffMonths <= 0) return "0y 0m";
            
            int years = diffMonths / 12;
            int months = diffMonths % 12;
            return years + "y " + months + "m";
        } catch (Exception e) {
            return "0y 0m";
        }
    }

    private String formatShortJoinDate(String joinDateStr) {
        if (joinDateStr == null || joinDateStr.isEmpty() || joinDateStr.equalsIgnoreCase("N/A")) return "N/A";
        try {
            java.util.Date joinDate = null;
            String[] formats = {"dd MMM yyyy", "MMMM yyyy", "MMM yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};
            for (String f : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.US);
                    joinDate = sdf.parse(joinDateStr);
                    if (joinDate != null) break;
                } catch (Exception ignored) {}
            }
            if (joinDate == null) return "N/A";
            return new SimpleDateFormat("MMM ''yy", Locale.US).format(joinDate);
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String calculateTenure(String joinDateStr) {
        if (joinDateStr == null || joinDateStr.isEmpty() || joinDateStr.equalsIgnoreCase("N/A")) return "N/A";
        try {
            String[] formats = {"dd MMM yyyy", "MMMM yyyy", "MMM yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};
            java.util.Date joinDate = null;
            for (String f : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.US);
                    joinDate = sdf.parse(joinDateStr);
                    if (joinDate != null) break;
                } catch (Exception ignored) {}
            }
            if (joinDate == null) return "N/A";

            java.util.Calendar join = java.util.Calendar.getInstance();
            join.setTime(joinDate);
            join.set(java.util.Calendar.HOUR_OF_DAY, 0);
            join.set(java.util.Calendar.MINUTE, 0);
            join.set(java.util.Calendar.SECOND, 0);
            join.set(java.util.Calendar.MILLISECOND, 0);

            java.util.Calendar now = java.util.Calendar.getInstance();
            now.set(java.util.Calendar.HOUR_OF_DAY, 0);
            now.set(java.util.Calendar.MINUTE, 0);
            now.set(java.util.Calendar.SECOND, 0);
            now.set(java.util.Calendar.MILLISECOND, 0);

            if (join.after(now)) return "Joined today";

            int diffMonths = (now.get(java.util.Calendar.YEAR) - join.get(java.util.Calendar.YEAR)) * 12 + 
                             (now.get(java.util.Calendar.MONTH) - join.get(java.util.Calendar.MONTH));
            
            if (now.get(java.util.Calendar.DAY_OF_MONTH) < join.get(java.util.Calendar.DAY_OF_MONTH)) {
                diffMonths--;
            }

            if (diffMonths < 0) return "Joined today";
            
            int years = diffMonths / 12;
            int months = diffMonths % 12;
            
            java.util.Calendar temp = (java.util.Calendar) join.clone();
            temp.add(java.util.Calendar.YEAR, years);
            temp.add(java.util.Calendar.MONTH, months);
            
            long diffMillis = now.getTimeInMillis() - temp.getTimeInMillis();
            int days = (int) (diffMillis / (24 * 60 * 60 * 1000));

            StringBuilder result = new StringBuilder();
            if (years > 0) result.append(years).append(years == 1 ? " year " : " years ");
            if (months > 0) result.append(months).append(months == 1 ? " month " : " months ");
            if (days > 0) result.append(days).append(days == 1 ? " day" : " days");

            String tenureStr = result.toString().trim();
            return tenureStr.isEmpty() ? "Joined today" : tenureStr;
        } catch (Exception e) {
            return "N/A";
        }
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
        
        RecyclerView rv = view.findViewById(R.id.rvSalaryHistory);
        androidx.recyclerview.widget.LinearLayoutManager lm = new androidx.recyclerview.widget.LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        
        List<BuaSalary> history = new java.util.ArrayList<>();
        Cursor c = db.getBuaSalaryHistory();
        if (c != null && c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String month = c.getString(c.getColumnIndexOrThrow("month_year"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
                String date = c.getString(c.getColumnIndexOrThrow("paid_date"));
                String status = c.getString(c.getColumnIndexOrThrow("status"));
                history.add(new BuaSalary(id, month, amount, date, status));
            } while (c.moveToNext());
            c.close();
        }
        
        BuaSalaryAdapter adapter = new BuaSalaryAdapter(history);
        rv.setAdapter(adapter);

        // Current Month Check
        String currentMonth = new SimpleDateFormat("MMMM yyyy", Locale.US).format(java.util.Calendar.getInstance().getTime());
        TextView tvWarning = view.findViewById(R.id.tvSalaryWarning); // The unpaid text
        View warningCard = view.findViewById(R.id.cardWarningUnpaid); // The orange card
        
        Button btnMarkPaid = view.findViewById(R.id.btnMarkPaid);
        if (db.isBuaSalaryPaid(currentMonth)) {
            btnMarkPaid.setVisibility(View.GONE);
            warningCard.setVisibility(View.GONE);
        } else {
            btnMarkPaid.setText("Mark " + currentMonth + " as Paid");
        }

        btnMarkPaid.setOnClickListener(v -> {
            double amount = 0;
            Cursor b = db.getBuaProfile();
            if (b != null && b.moveToFirst()) {
                amount = b.getDouble(b.getColumnIndexOrThrow("salary"));
                b.close();
            }
            String today = new SimpleDateFormat("dd MMM", Locale.US).format(java.util.Calendar.getInstance().getTime());
            db.addBuaSalaryPayment(currentMonth, amount, today);
            Toast.makeText(this, "Salary Marked as Paid", Toast.LENGTH_SHORT).show();
            showSalary(); // Refresh
        });

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
