package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MealRoutineActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String currentUserEmail;
    private String todayDate;
    private int b = 0, l = 0, d = 0;
    private boolean isAdmin = false;

    private TextView tvB, tvL, tvD;
    private TextView tvTodayDate, tvMyTotal, tvMyTotalCost;
    private LinearLayout routineContainer, adminDashboard, memberControls, adminMemberBreakdown;

    // Admin Dashboard Views
    private TextView tvAdminBTotal, tvAdminBBreakdown;
    private TextView tvAdminLTotal, tvAdminLBreakdown;
    private TextView tvAdminDTotal, tvAdminDBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_routine);

        db = new DatabaseHelper(this);
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = pref.getString("email", "anonymous");
        isAdmin = "Admin".equalsIgnoreCase(pref.getString("role", "Member"));

        Calendar cal = Calendar.getInstance();
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());

        initViews();
        
        if (isAdmin) {
            memberControls.setVisibility(View.GONE);
            adminDashboard.setVisibility(View.VISIBLE);
            loadAdminData();
        } else {
            loadTodayStatus();
            updateSummary();
        }
        
        loadMealHistory();
        setupNavigation();
    }

    private void initViews() {
        tvB = findViewById(R.id.tv_b_count);
        tvL = findViewById(R.id.tv_l_count);
        tvD = findViewById(R.id.tv_d_count);
        tvTodayDate = findViewById(R.id.tv_today_date);
        
        tvMyTotal = findViewById(R.id.tv_my_total_meals);
        tvMyTotalCost = findViewById(R.id.tv_my_total_cost);
        
        routineContainer = findViewById(R.id.routine_container);
        adminDashboard = findViewById(R.id.admin_dashboard_container);
        memberControls = findViewById(R.id.member_meal_controls);
        adminMemberBreakdown = findViewById(R.id.admin_member_breakdown_container);

        // Admin Views
        tvAdminBTotal = findViewById(R.id.tv_admin_b_total);
        tvAdminBBreakdown = findViewById(R.id.tv_admin_b_breakdown);
        tvAdminLTotal = findViewById(R.id.tv_admin_l_total);
        tvAdminLBreakdown = findViewById(R.id.tv_admin_l_breakdown);
        tvAdminDTotal = findViewById(R.id.tv_admin_d_total);
        tvAdminDBreakdown = findViewById(R.id.tv_admin_d_breakdown);

        tvTodayDate.setText(new SimpleDateFormat("EEEE, dd MMM", Locale.US).format(Calendar.getInstance().getTime()));

        findViewById(R.id.btn_b_plus).setOnClickListener(v -> updateMeal("B", 1));
        findViewById(R.id.btn_b_minus).setOnClickListener(v -> updateMeal("B", -1));
        findViewById(R.id.btn_l_plus).setOnClickListener(v -> updateMeal("L", 1));
        findViewById(R.id.btn_l_minus).setOnClickListener(v -> updateMeal("L", -1));
        findViewById(R.id.btn_d_plus).setOnClickListener(v -> updateMeal("D", 1));
        findViewById(R.id.btn_d_minus).setOnClickListener(v -> updateMeal("D", -1));
    }

    private void loadAdminData() {
        // Today's Breakdown
        updateAdminRow(todayDate, "Breakfast", tvAdminBTotal, tvAdminBBreakdown);
        updateAdminRow(todayDate, "Lunch", tvAdminLTotal, tvAdminLBreakdown);
        updateAdminRow(todayDate, "Dinner", tvAdminDTotal, tvAdminDBreakdown);

        // Member-wise details
        adminMemberBreakdown.removeAllViews();
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        double fixedRate = pref.getFloat("fixed_meal_rate", 0.0f);
        
        Cursor cursor = db.getMemberMealDetailsForDate(todayDate);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                int bVal = cursor.getInt(1);
                int lVal = cursor.getInt(2);
                int dVal = cursor.getInt(3);
                int totalMeals = bVal + lVal + dVal;
                int dailyCost = (int) (totalMeals * fixedRate);
                
                String mealStr = "B:" + bVal + " | L:" + lVal + " | D:" + dVal;
                if (fixedRate > 0) {
                    mealStr += " (৳" + dailyCost + ")";
                }
                
                addAdminBreakdownRow(name, mealStr);
            }
            cursor.close();
        }
        if (adminMemberBreakdown.getChildCount() == 0) {
            addAdminBreakdownRow("No member meals recorded yet.", "");
        }
    }

    private void updateAdminRow(String date, String type, TextView totalTv, TextView breakdownTv) {
        int members = db.getMemberMealCount(date, type);
        int guests = db.getGuestMealCount(date, type);
        int total = members + guests;

        totalTv.setText(total + " Meals");
        breakdownTv.setText(members + " Members + " + guests + " Guests");
    }

    private void addAdminBreakdownRow(String name, String meals) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);
        TextView nText = new TextView(this); nText.setText(name); nText.setTextColor(Color.BLACK); nText.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        TextView mText = new TextView(this); mText.setText(meals); mText.setTextColor(Color.parseColor("#1B7A9E")); mText.setGravity(android.view.Gravity.END);
        row.addView(nText); row.addView(mText);
        adminMemberBreakdown.addView(row);
    }

    private void loadTodayStatus() {
        Cursor cursor = db.getMealStatus(currentUserEmail, todayDate);
        if (cursor != null && cursor.moveToFirst()) {
            b = cursor.getInt(cursor.getColumnIndexOrThrow("breakfast"));
            l = cursor.getInt(cursor.getColumnIndexOrThrow("lunch"));
            d = cursor.getInt(cursor.getColumnIndexOrThrow("dinner"));
            cursor.close();
        }
        updateCounts();
    }

    private void updateMeal(String type, int delta) {
        if (type.equals("B")) b = Math.max(0, b + delta);
        else if (type.equals("L")) l = Math.max(0, l + delta);
        else if (type.equals("D")) d = Math.max(0, d + delta);

        db.updateDailyMeals(currentUserEmail, todayDate, b, l, d);
        updateCounts();
        updateSummary();
        loadMealHistory(); 
    }

    private void updateCounts() {
        if (tvB != null) tvB.setText(String.valueOf(b));
        if (tvL != null) tvL.setText(String.valueOf(l));
        if (tvD != null) tvD.setText(String.valueOf(d));
    }

    private void updateSummary() {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentName = sharedPref.getString("name", "User");
        int myTotal = db.getUserTotalMeals(currentUserEmail, currentName);
        tvMyTotal.setText(String.valueOf(myTotal));

        double fixedRate = sharedPref.getFloat("fixed_meal_rate", 0.0f);
        double totalCost = myTotal * fixedRate;
        tvMyTotalCost.setText("৳" + (int) totalCost);
    }

    private void loadMealHistory() {
        routineContainer.removeAllViews();
        
        Cursor cursor;
        if (isAdmin) {
            // Admin sees global totals for previous days
            cursor = db.getGlobalMealHistory();
            findViewById(R.id.tv_history_title).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tv_history_title)).setText("GLOBAL MEAL HISTORY");
            findViewById(R.id.history_card).setVisibility(View.VISIBLE);
        } else {
            // Member sees personal history
            SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String currentName = pref.getString("name", "User");
            cursor = db.getUserMealHistory(currentUserEmail, currentName);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);

                int bVal = cursor.getInt(1);
                int lVal = cursor.getInt(2);
                int dVal = cursor.getInt(3);

                String mealStr = "B:" + bVal + " | L:" + lVal + " | D:" + dVal;
                String formattedDate = date;
                try {
                    java.util.Date dObj = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
                    if (dObj != null) formattedDate = new SimpleDateFormat("dd MMM, EEEE", Locale.US).format(dObj);
                } catch (Exception ignored) {}

                addRoutineRow(formattedDate, mealStr);
            }
            cursor.close();
        }
        
        if (routineContainer.getChildCount() == 0) {
            TextView empty = new TextView(this);
            empty.setText("No history found.");
            empty.setTextColor(Color.GRAY);
            empty.setGravity(android.view.Gravity.CENTER);
            empty.setPadding(0, 40, 0, 40);
            routineContainer.addView(empty);
        }
    }

    private void addRoutineRow(String day, String meal) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 10, 0, 10);
        TextView dText = new TextView(this); dText.setText(day); dText.setTextColor(Color.BLACK); dText.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        TextView mText = new TextView(this); mText.setText(meal); mText.setTextColor(Color.GRAY); mText.setGravity(android.view.Gravity.END);
        row.addView(dText); row.addView(mText);
        routineContainer.addView(row);
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> { startActivity(new Intent(this, MemberActivity.class)); finish(); });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> { startActivity(new Intent(this, BazarActivity.class)); finish(); });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> { startActivity(new Intent(this, CashLedgerActivity.class)); finish(); });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> { startActivity(new Intent(this, AllFeaturesActivity.class)); finish(); });
    }
}
