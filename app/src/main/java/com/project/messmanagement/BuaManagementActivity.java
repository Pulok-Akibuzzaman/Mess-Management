package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import android.widget.LinearLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BuaManagementActivity extends AppCompatActivity {

    private Button btnProfile, btnSalary, btnSchedule;
    private FrameLayout tabContent;
    private DatabaseHelper db;
    private boolean isAdmin = false;

    private TextView tvNameHeader, tvPhoneHeader, tvAddressHeader, tvSalaryStat;
    private TextView tvServiceStat, tvJoinedStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bua_management);

        db = new DatabaseHelper(this);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        
        initViews();
        loadBuaData();
        setupNavigation();

        btnProfile.setOnClickListener(v -> { setActiveTab(btnProfile); showProfile(); });
        btnSalary.setOnClickListener(v -> { setActiveTab(btnSalary); showSalary(); });
        btnSchedule.setOnClickListener(v -> { setActiveTab(btnSchedule); showSchedule(); });

        View btnEdit = findViewById(R.id.btnEditBua);
        if (isAdmin) {
            btnEdit.setOnClickListener(v -> showEditBuaDialog());
        } else {
            btnEdit.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBuaData();
        fetchBuaCloudData();
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
        } else {
            tvNameHeader.setText("No Bua Assigned");
            tvPhoneHeader.setText("N/A");
            tvAddressHeader.setText("N/A");
            tvSalaryStat.setText("৳0");
            tvServiceStat.setText("0y 0m");
            tvJoinedStat.setText("N/A");
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
        Button btnDelete = view.findViewById(R.id.btnDeleteBuaProfile); // New delete button
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        if (btnDelete != null) {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(this)
                    .setTitle("Remove Bua")
                    .setMessage("Are you sure you want to remove all Bua information?")
                    .setPositiveButton("Remove", (d, w) -> {
                        db.deleteBuaProfile();
                        
                        // Sync Delete to Supabase
                        RemoteAccess.getInstance().syncActionToSupabase("bua_profile", "DELETE", null, "id=eq.1");

                        dialog.dismiss();
                        loadBuaData();
                        showProfile();
                        Toast.makeText(this, "Bua Information Removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            });
        }

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
                double salary = Double.parseDouble(salaryStr);
                db.updateBuaProfile(name, phone, address, salary, joinDate);
                
                // Sync to Supabase using UPSERT
                String json = "{" +
                        "\"id\": 1," +
                        "\"name\": \"" + name + "\"," +
                        "\"phone\": \"" + phone + "\"," +
                        "\"address\": \"" + address + "\"," +
                        "\"salary\": " + salary + "," +
                        "\"join_date\": \"" + joinDate + "\"" +
                        "}";
                RemoteAccess.getInstance().upsertToSupabase("bua_profile", json);

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
            
            cursor.close();
        } else {
            setRow(view, R.id.rowFullName,  "Full Name", "Not Assigned");
            setRow(view, R.id.rowPhone,     "Phone",     "N/A");
            setRow(view, R.id.rowAddress,   "Address",   "N/A");
            setRow(view, R.id.rowJoinDate,  "Join Date", "N/A");
            setRow(view, R.id.rowTenure,    "Service Time", "0 days");
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
            if (isAdmin) {
                btnMarkPaid.setText("Mark " + currentMonth + " as Paid");
            } else {
                btnMarkPaid.setVisibility(View.GONE);
                // Keep the warning card but remove the button for members
                tvWarning.setText(currentMonth + " salary is not paid yet.");
            }
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
            
            // Sync to Supabase
            String json = "{" +
                    "\"month_year\": \"" + currentMonth + "\"," +
                    "\"amount\": " + amount + "," +
                    "\"paid_date\": \"" + today + "\"," +
                    "\"status\": \"Paid\"" +
                    "}";
            RemoteAccess.getInstance().syncToSupabase("bua_salary_history", json);

            // Sync with Ledger
            SharedPreferences prefUser = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String adminName = prefUser.getString("name", "Admin");
            db.addCashTransaction("Bua Salary: " + currentMonth, amount, "OUT", today, adminName, "");
            
            Toast.makeText(this, "Salary Marked as Paid & Recorded in Ledger", Toast.LENGTH_SHORT).show();
            showSalary(); // Refresh
        });

        tabContent.addView(view);
    }

    private void showSchedule() {
        tabContent.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_schedule, tabContent, false);
        
        TextView tvB = view.findViewById(R.id.tvCookB);
        TextView tvL = view.findViewById(R.id.tvCookL);
        TextView tvD = view.findViewById(R.id.tvCookD);
        TextView tvDate = view.findViewById(R.id.tvTodayDate);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Calendar.getInstance().getTime());
        String displayDate = new SimpleDateFormat("EEEE, dd MMM", Locale.US).format(java.util.Calendar.getInstance().getTime());
        
        if (tvDate != null) tvDate.setText(displayDate);
        
        int[] counts = db.getTodaysMealCounts(today);
        if (tvB != null) tvB.setText(String.valueOf(counts[0]));
        if (tvL != null) tvL.setText(String.valueOf(counts[1]));
        if (tvD != null) tvD.setText(String.valueOf(counts[2]));

        // Set Schedule Rows
        setScheduleRow(view, R.id.rowMorning, "Morning Cleaning", "7:00–9:00 AM", "Sat–Thu");
        setScheduleRow(view, R.id.rowCooking, "Lunch Cooking", "11:00–1:30 PM", "Daily");
        setScheduleRow(view, R.id.rowDish,    "Dish Washing",  "2:00–3:00 PM",  "Daily");
        setScheduleRow(view, R.id.rowEvening, "Dinner Cooking", "6:30–8:30 PM", "Daily");
        setScheduleRow(view, R.id.rowDeep,    "Deep Cleaning", "10:00–1:00 PM", "Friday");

        tabContent.addView(view);
    }

    private void setScheduleRow(View parent, int rowId, String task, String time, String days) {
        View row = parent.findViewById(rowId);
        if (row != null) {
            ((TextView) row.findViewById(R.id.tvTaskName)).setText(task);
            ((TextView) row.findViewById(R.id.tvTimeSlot)).setText(time);
            ((TextView) row.findViewById(R.id.tvDays)).setText(days);
        }
    }

    private void setupNavigation() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sp.getString("role", "Member");
        boolean isBuaRole = "Bua".equalsIgnoreCase(role);

        if (isBuaRole) {
            findViewById(R.id.btn_bazar_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_cash_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_meals_layout).setVisibility(View.GONE);
            
            LinearLayout btnSalaryNav = findViewById(R.id.btn_member_layout);
            if (btnSalaryNav != null) {
                TextView tv = (TextView) btnSalaryNav.getChildAt(1);
                tv.setText("Salary");
                tv.setTextColor(getResources().getColor(R.color.nav_active));
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                
                ImageView iv = (ImageView) btnSalaryNav.getChildAt(0);
                iv.setImageResource(R.drawable.ic_briefcase);
                iv.setColorFilter(getResources().getColor(R.color.nav_active));
            }
        } else {
            // If Admin/Member is here, it's a sub-feature of 'More'
            LinearLayout btnMoreNav = findViewById(R.id.btn_more_layout);
            if (btnMoreNav != null) {
                TextView tv = (TextView) btnMoreNav.getChildAt(1);
                tv.setTextColor(getResources().getColor(R.color.nav_active));
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                
                ImageView iv = (ImageView) btnMoreNav.getChildAt(0);
                iv.setColorFilter(getResources().getColor(R.color.nav_active));
            }
        }

        findViewById(R.id.btn_home_layout).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> { 
            if (isBuaRole) {
                // Already here
            } else {
                startActivity(new Intent(this, MemberActivity.class)); 
            }
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> startActivity(new Intent(this, MealRoutineActivity.class)));
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> startActivity(new Intent(this, BazarActivity.class)));
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> startActivity(new Intent(this, CashLedgerActivity.class)));
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
    }

    private void fetchBuaCloudData() {
        new Thread(() -> {
            // Fetch Profile
            String pResp = RemoteAccess.getInstance().syncFromSupabase("bua_profile", "id=eq.1");
            if (pResp != null && !pResp.isEmpty()) {
                try {
                    JSONArray arr = new JSONArray(pResp);
                    if (arr.length() > 0) {
                        JSONObject obj = arr.getJSONObject(0);
                        db.updateBuaProfile(obj.getString("name"), obj.getString("phone"), 
                                          obj.getString("address"), obj.getDouble("salary"), obj.getString("join_date"));
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }

            // Fetch Salary History
            String sResp = RemoteAccess.getInstance().syncFromSupabase("bua_salary_history", "order=id.desc");
            if (sResp != null && !sResp.isEmpty()) {
                try {
                    JSONArray arr = new JSONArray(sResp);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String month = obj.getString("month_year");
                        if (!db.isBuaSalaryPaid(month)) {
                            db.addBuaSalaryPayment(month, obj.getDouble("amount"), obj.getString("paid_date"));
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
            runOnUiThread(this::loadBuaData);
        }).start();
    }
}
