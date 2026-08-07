package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.net.Uri;
import android.database.Cursor;
import android.view.View;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    // 1. Declare UI Elements
    TextView tvWelcome, tvTotalAmount, tvMealRate, tvActiveMembers, tvCashBalance, tvMonthLabel;
    TextView tvTotalMealsGrid, tvBazarSpentGrid, tvUtilitiesGrid, tvMyTotalBillGrid;
    TextView tvBazarLabelVal, tvUtilityLabelVal, tvBuaLabelVal, tvOtherLabelVal;
    TextView tvMyBillSubtitle, tvBazarSpentSubtitle, tvUtilitiesSubtitle;
    BarChart barChart;
    PieChart pieChart;
    LinearLayout btnBazar, btnCash, btnMeals, btnMore, btnMember;
    ImageView btnLogout;
    android.widget.FrameLayout btnNotification;
    
    String currentUserEmail;
    
    // 2. Declare Database Helper
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3. Initialize DB and Views
        db = new DatabaseHelper(this);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = pref.getString("email", "anonymous");
        
        initViews();
        
        // 4. Navigation
        setupNavigation();
        checkSafetyTimer();
    }

    private void checkSafetyTimer() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long threshold = pref.getLong("sos_timeout_millis", 0);
        if (threshold > 0) {
            long lastActivity = pref.getLong("last_activity_time", System.currentTimeMillis());
            if (System.currentTimeMillis() - lastActivity > threshold) {
                // Timer Expired!
                triggerEmergencyDial();
            }
        }
    }

    private void triggerEmergencyDial() {
        Cursor cursor = db.getAllEmergencyContacts();
        if (cursor != null && cursor.moveToFirst()) {
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            cursor.close();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
            Toast.makeText(this, "SAFETY ALERT: Inactivity Timeout Reached!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recalculate and update UI every time the user sees this screen
        loadDashboardData();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvMonthLabel = findViewById(R.id.tv_month_label);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvMealRate = findViewById(R.id.tv_meal_rate);
        tvActiveMembers = findViewById(R.id.tv_active_members);
        tvCashBalance = findViewById(R.id.tv_cash_balance);
        
        // Grid Stats
        tvTotalMealsGrid = findViewById(R.id.tv_total_meals);
        tvBazarSpentGrid = findViewById(R.id.tv_bazar_spent);
        tvUtilitiesGrid = findViewById(R.id.tv_utilities_grid);
        tvMyTotalBillGrid = findViewById(R.id.tv_avg_per_head); // Reusing this ID for Individual Bill
        tvMyBillSubtitle = findViewById(R.id.tv_avg_per_head_subtitle);
        tvBazarSpentSubtitle = findViewById(R.id.tv_bazar_spent_subtitle);
        tvUtilitiesSubtitle = findViewById(R.id.tv_utilities_subtitle);

        // Role-based UI simplification for Bua
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userRole = pref.getString("role", "Admin");
        if ("Bua".equalsIgnoreCase(userRole)) {
            // 1. Hide unwanted navbar items
            findViewById(R.id.btn_bazar_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_cash_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_meals_layout).setVisibility(View.GONE);
            
            // 2. Repurpose Member button as Salary
            LinearLayout btnMember = findViewById(R.id.btn_member_layout);
            if (btnMember != null) {
                TextView tv = (TextView) btnMember.getChildAt(1);
                tv.setText("Salary");
                ImageView iv = (ImageView) btnMember.getChildAt(0);
                iv.setImageResource(R.drawable.ic_briefcase); // Work/Salary icon
            }

            // 3. Hide specific statistics cards in grid
            findViewById(R.id.grid_stats).setVisibility(View.GONE);
        }

        // Label values in Breakdown
        tvBazarLabelVal = findViewById(R.id.tv_bazar_label_val);
        tvUtilityLabelVal = findViewById(R.id.tv_utility_label_val);
        tvBuaLabelVal = findViewById(R.id.tv_bua_label_val);
        tvOtherLabelVal = findViewById(R.id.tv_other_label_val);

        barChart = findViewById(R.id.bar_chart_meals);
        pieChart = findViewById(R.id.pie_chart_expenses);

        btnBazar = findViewById(R.id.btn_bazar_layout);
        btnCash = findViewById(R.id.btn_cash_layout);
        btnMember = findViewById(R.id.btn_member_layout);
        btnMeals = findViewById(R.id.btn_meals_layout);
        btnMore = findViewById(R.id.btn_more_layout);
        btnLogout = findViewById(R.id.btn_logout);
        btnNotification = findViewById(R.id.fl_notification);

        // Set Dynamic Month Label
        Calendar cal = Calendar.getInstance();
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int year = cal.get(Calendar.YEAR);
        if (tvMonthLabel != null) tvMonthLabel.setText(month.toUpperCase() + " " + year + " TOTAL");

        // Set Welcome Name
        String name = getIntent().getStringExtra("USER_NAME");
        String role = getIntent().getStringExtra("USER_ROLE");
        if (name != null) tvWelcome.setText("Welcome back, " + name + " (" + (role != null ? role : "Admin") + ")");
    }

    private void loadDashboardData() {
        // Fetch values from SQLite
        double totalBazar = db.getTotalBazar();
        int totalMessMeals = db.getTotalMeals();
        double utilities = db.getUtilitiesTotal();
        int activeMembers = db.getActiveMembersCount();
        double cashBalance = db.getCashBalance();
        int myMeals = db.getUserTotalMeals(currentUserEmail);

        double buaSalary = 0;
        Cursor buaCursor = db.getBuaProfile();
        if (buaCursor != null && buaCursor.moveToFirst()) {
            buaSalary = buaCursor.getDouble(buaCursor.getColumnIndexOrThrow("salary"));
            buaCursor.close();
        }

        // Individual Calculations
        double mealRate = totalMessMeals > 0 ? totalBazar / totalMessMeals : 0;
        double fixedCostPerHead = activeMembers > 0 ? (utilities + buaSalary) / activeMembers : 0; 
        double myTotalBill = (myMeals * mealRate) + fixedCostPerHead;
        
        double totalMessExpense = totalBazar + utilities + buaSalary;

        // Update UI
        tvTotalAmount.setText("৳" + (int)totalMessExpense);
        tvMealRate.setText("৳" + String.format(Locale.getDefault(), "%.1f", mealRate));
        tvActiveMembers.setText(String.valueOf(activeMembers));
        tvCashBalance.setText("৳" + (int)cashBalance);
        
        // Update Grid
        if (tvTotalMealsGrid != null) tvTotalMealsGrid.setText(String.valueOf(totalMessMeals));
        if (tvBazarSpentGrid != null) tvBazarSpentGrid.setText("৳" + (int)totalBazar);
        if (tvBazarSpentSubtitle != null) tvBazarSpentSubtitle.setText(db.getBazarCount() + " purchases");
        if (tvUtilitiesGrid != null) tvUtilitiesGrid.setText("৳" + (int)utilities);
        if (tvUtilitiesSubtitle != null) tvUtilitiesSubtitle.setText(db.getUtilitiesCount() + " bills paid");
        
        // --- THIS IS NOW INDIVIDUAL ---
        if (tvMyTotalBillGrid != null) tvMyTotalBillGrid.setText("৳" + (int)myTotalBill);
        if (tvMyBillSubtitle != null) tvMyBillSubtitle.setText("My share (" + myMeals + " meals)");

        // --- BUA SPECIFIC DASHBOARD ---
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userRole = pref.getString("role", "Admin");
        if ("Bua".equalsIgnoreCase(userRole)) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
            int[] counts = db.getTodaysMealCounts(today);
            
            // Re-purposing Welcome Text to show cooking counts
            tvWelcome.setText("Cooking for Today: " + 
                "\nBreakfast: " + counts[0] + " | Lunch: " + counts[1] + " | Dinner: " + counts[2]);
            
            // Hide financial breakdown for Bua
            if (tvTotalAmount != null) tvTotalAmount.setText("Mess Active");
            findViewById(R.id.card_expense_chart).setVisibility(View.GONE);
            findViewById(R.id.card_weekly_chart).setVisibility(View.GONE);
            findViewById(R.id.tv_weekly_meal_title).setVisibility(View.GONE);
            findViewById(R.id.tv_expense_title).setVisibility(View.GONE);
        }

        // Update Breakdown Labels
        if (tvBazarLabelVal != null) tvBazarLabelVal.setText("৳" + (int)totalBazar);
        if (tvUtilityLabelVal != null) tvUtilityLabelVal.setText("৳" + (int)utilities);
        if (tvBuaLabelVal != null) tvBuaLabelVal.setText("৳" + (int)buaSalary);
        if (tvOtherLabelVal != null) tvOtherLabelVal.setText("৳0");

        setupCharts(totalMessMeals, totalBazar, utilities);
    }

    private void setupCharts(int meals, double bazar, double util) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 15f));
        barEntries.add(new BarEntry(1, 20f));
        barEntries.add(new BarEntry(2, meals/10f));
        
        BarDataSet barDataSet = new BarDataSet(barEntries, "Meals");
        barDataSet.setColor(ContextCompat.getColor(this, R.color.admin_chart_blue));
        barChart.setData(new BarData(barDataSet));
        barChart.invalidate();

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) bazar, "Bazar"));
        pieEntries.add(new PieEntry((float) util, "Utility"));
        pieEntries.add(new PieEntry(4000f, "Bua"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(new int[]{R.color.admin_chart_blue, R.color.admin_chart_orange, R.color.admin_chart_green}, this);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.invalidate();
    }

    private void setupNavigation() {
        btnBazar.setOnClickListener(v -> startActivity(new Intent(this, BazarActivity.class)));
        btnCash.setOnClickListener(v -> startActivity(new Intent(this, CashLedgerActivity.class)));
        
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String r = sp.getString("role", "Admin");

        if ("Bua".equalsIgnoreCase(r)) {
            btnMember.setOnClickListener(v -> startActivity(new Intent(this, BuaManagementActivity.class)));
        } else {
            btnMember.setOnClickListener(v -> startActivity(new Intent(this, MemberActivity.class)));
        }

        btnMeals.setOnClickListener(v -> startActivity(new Intent(this, MealRoutineActivity.class)));
        btnMore.setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
        
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                startActivity(new Intent(this, NoticesActivity.class));
            });
            if ("Bua".equalsIgnoreCase(r)) btnNotification.setVisibility(View.GONE);
        }
        
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // 1. Clear Login State
                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                
                // 2. Go back to Login
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
