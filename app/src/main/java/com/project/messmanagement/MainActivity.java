package com.project.messmanagement;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.net.Uri;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    // 1. Declare UI Elements
    TextView tvWelcome, tvTotalAmount, tvActiveMembers, tvCashBalance, tvMonthLabel;
    TextView tvTotalMealsGrid, tvBazarSpentGrid, tvUtilitiesGrid;
    TextView tvBazarLabelVal, tvUtilityLabelVal, tvBuaLabelVal, tvOtherLabelVal;
    TextView tvBazarSpentSubtitle, tvUtilitiesSubtitle, tvCurrentMealRate, tvEditRateHint;
    PieChart pieChart;
    LinearLayout btnBazar, btnCash, btnMeals, btnMore, btnMember;
    ImageView btnLogout;
    android.widget.FrameLayout btnNotification;
    View cardMealRate;
    
    String currentUserEmail;
    
    // 2. Declare Database Helper
    private DatabaseHelper db;

    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

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

    @Override
    protected void onStart(){
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
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

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvMonthLabel = findViewById(R.id.tv_month_label);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvActiveMembers = findViewById(R.id.tv_active_members);
        tvCashBalance = findViewById(R.id.tv_cash_balance);
        
        // Grid Stats
        tvTotalMealsGrid = findViewById(R.id.tv_total_meals);
        tvBazarSpentGrid = findViewById(R.id.tv_bazar_spent);
        tvUtilitiesGrid = findViewById(R.id.tv_utilities_grid);
        tvBazarSpentSubtitle = findViewById(R.id.tv_bazar_spent_subtitle);
        tvUtilitiesSubtitle = findViewById(R.id.tv_utilities_subtitle);

        tvCurrentMealRate = findViewById(R.id.tv_current_meal_rate);
        tvEditRateHint = findViewById(R.id.tv_edit_rate_hint);
        cardMealRate = findViewById(R.id.card_meal_rate);

        // Role-based UI simplification for Bua
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userRole = pref.getString("role", "Member");
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
            if (cardMealRate != null) cardMealRate.setVisibility(View.GONE);

            // 4. Hide financial stats in main card
            findViewById(R.id.layout_cash_balance).setVisibility(View.GONE);
        }

        // Label values in Breakdown
        tvBazarLabelVal = findViewById(R.id.tv_bazar_label_val);
        tvUtilityLabelVal = findViewById(R.id.tv_utility_label_val);
        tvBuaLabelVal = findViewById(R.id.tv_bua_label_val);
        tvOtherLabelVal = findViewById(R.id.tv_other_label_val);

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

        // Set Welcome Name & Role with Fallback
        String name = getIntent().getStringExtra("USER_NAME");
        String role = getIntent().getStringExtra("USER_ROLE");
        
        if (name == null || role == null) {
            name = pref.getString("name", "User");
            role = pref.getString("role", "Admin");
        }
        
        String firstName = name.trim().split(" ")[0];
        tvWelcome.setText("Welcome back, " + firstName + " (" + role + ")");
    }

    private void loadDashboardData() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // Reset dynamic labels first
        Calendar cal = Calendar.getInstance();
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int year = cal.get(Calendar.YEAR);
        if (tvMonthLabel != null) tvMonthLabel.setText(month.toUpperCase() + " " + year + " TOTAL");
        if (tvTotalAmount != null) tvTotalAmount.setTextSize(40); // Reset font size

        // Fetch values from SQLite
        double totalBazar = db.getTotalBazar();
        int totalMessMeals = db.getTotalMeals();
        double utilities = db.getUtilitiesTotal();
        int residentCount = db.getResidentCount();
        int activeCount = db.getActiveMemberCount();
        double cashBalance = db.getCashBalance();
        
        String currentUserName = pref.getString("name", "User");
        int myMeals = db.getUserTotalMeals(currentUserEmail, currentUserName);

        double buaSalary = db.getBuaSalary();
        double houseRent = db.getHouseRent();
        double occasionCost = db.getTotalOccasionCost();
        double otherExpenses = db.getUtilityTotalByType("Others");

        // Fixed Rate Calculation
        double fixedMealRate = pref.getFloat("fixed_meal_rate", 0.0f);
        
        String userRole = pref.getString("role", "Member");
        
        // Split Calculation
        double sharedUtilityPerPerson = residentCount > 0 ? (utilities + buaSalary + houseRent + otherExpenses) / residentCount : 0;
        double occasionPerPerson = activeCount > 0 ? occasionCost / activeCount : 0;
        
        double mySharedTotal = sharedUtilityPerPerson;
        if ("Active".equalsIgnoreCase(userRole)) {
            mySharedTotal += occasionPerPerson;
        }
        
        double myTotalBill = (myMeals * fixedMealRate) + mySharedTotal;
        
        double totalMessExpense = totalBazar + utilities + buaSalary + houseRent + occasionCost + otherExpenses;

        if ("Admin".equalsIgnoreCase(userRole)) {
            // Admin sees the big picture
            tvTotalAmount.setText("৳" + (int)totalMessExpense);
            if (tvMonthLabel != null) tvMonthLabel.setText(month.toUpperCase() + " " + year + " MESS TOTAL");
            
            // Fixed rate can only be changed via the dedicated meal rate card
            tvTotalAmount.setOnClickListener(null); 
            if (cardMealRate != null) {
                cardMealRate.setOnClickListener(v -> showUpdateRateDialog(fixedMealRate));
                if (tvEditRateHint != null) tvEditRateHint.setVisibility(View.VISIBLE);
            }
            tvCashBalance.setText("৳" + (int)cashBalance);
        } else {
            // Member sees their OWN personal due as the main focus
            double myPaid = db.getMemberPaidAmount(currentUserEmail);
            double myNetDue = myTotalBill - myPaid;
            
            tvTotalAmount.setText("৳" + (int)myNetDue);
            if (tvMonthLabel != null) tvMonthLabel.setText("YOUR REMAINING DUE");
            tvTotalAmount.setOnClickListener(null);
            if (cardMealRate != null) {
                cardMealRate.setOnClickListener(null);
                if (tvEditRateHint != null) tvEditRateHint.setVisibility(View.GONE);
            }

            // Change cash balance label to show their personal paid total
            tvCashBalance.setText("৳" + (int)myPaid);
            View cashLayout = findViewById(R.id.layout_cash_balance);
            if (cashLayout != null) {
                ((TextView)((LinearLayout)cashLayout).getChildAt(0)).setText("You have paid");
            }
        }

        tvActiveMembers.setText(String.valueOf(residentCount));
        
        // Update Grid
        if (tvTotalMealsGrid != null) {
            if ("Admin".equalsIgnoreCase(userRole)) {
                tvTotalMealsGrid.setText(String.valueOf(totalMessMeals));
                // Set label to "Total meals"
                ((TextView)((LinearLayout)tvTotalMealsGrid.getParent()).getChildAt(2)).setText("Total meals");
            } else {
                tvTotalMealsGrid.setText(String.valueOf(myMeals));
                // Set label to "My meals"
                ((TextView)((LinearLayout)tvTotalMealsGrid.getParent()).getChildAt(2)).setText("My meals");
            }
        }
        if (tvBazarSpentGrid != null) tvBazarSpentGrid.setText("৳" + (int)totalBazar);
        if (tvBazarSpentSubtitle != null) tvBazarSpentSubtitle.setText(db.getBazarCount() + " purchases");
        if (tvUtilitiesGrid != null) {
            double sharedTotal = utilities + buaSalary + houseRent;
            double sharedCollected = db.getTotalBillsCollected();
            tvUtilitiesGrid.setText("৳" + (int)sharedCollected + " / ৳" + (int)sharedTotal);
            tvUtilitiesGrid.setTextSize(16); // Make it fit
        }
        if (tvUtilitiesSubtitle != null) {
            tvUtilitiesSubtitle.setText("Collected vs Goal");
            tvUtilitiesSubtitle.setTextColor(ContextCompat.getColor(this, R.color.admin_icon_teal));
        }
        
        // --- FIXED MEAL RATE DISPLAY ---
        if (tvCurrentMealRate != null) {
            tvCurrentMealRate.setText("৳" + String.format(Locale.US, "%.1f", fixedMealRate));
        }

        // --- BUA SPECIFIC DASHBOARD ---
        if ("Bua".equalsIgnoreCase(userRole)) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
            int[] counts = db.getTodaysMealCounts(today);
            
            // Show cooking counts in the big card instead of overwriting Welcome
            if (tvMonthLabel != null) tvMonthLabel.setText("TODAY'S COOKING REQUIRED");
            if (tvTotalAmount != null) {
                tvTotalAmount.setTextSize(24); // Slightly smaller to fit 3 counts
                tvTotalAmount.setText("B: " + counts[0] + "  |  L: " + counts[1] + "  |  D: " + counts[2]);
            }
            
            findViewById(R.id.card_expense_chart).setVisibility(View.GONE);
            findViewById(R.id.tv_expense_title).setVisibility(View.GONE);
        }

        // Update Breakdown Labels
        if (tvBazarLabelVal != null) tvBazarLabelVal.setText("৳" + (int)totalBazar);
        if (tvUtilityLabelVal != null) tvUtilityLabelVal.setText("৳" + (int)utilities);
        if (tvBuaLabelVal != null) tvBuaLabelVal.setText("৳" + (int)buaSalary);
        if (tvOtherLabelVal != null) tvOtherLabelVal.setText("৳" + (int)(houseRent + occasionCost));

        setupCharts(totalBazar, utilities, buaSalary, houseRent + occasionCost);
    }

    private void showUpdateRateDialog(double currentRate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MEAL PRICE");
        
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(currentRate));
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String val = input.getText().toString();
            if (!val.isEmpty()) {
                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putFloat("fixed_meal_rate", Float.parseFloat(val));
                editor.apply();
                loadDashboardData();
                Toast.makeText(this, "Rate updated to ৳" + val, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setupCharts(double bazar, double util, double buaSalary, double houseRent) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (bazar > 0) {
            pieEntries.add(new PieEntry((float) bazar, "Bazar"));
            colors.add(ContextCompat.getColor(this, R.color.admin_chart_blue));
        }
        if (util > 0) {
            pieEntries.add(new PieEntry((float) util, "Utility"));
            colors.add(ContextCompat.getColor(this, R.color.admin_chart_orange));
        }
        if (buaSalary > 0) {
            pieEntries.add(new PieEntry((float) buaSalary, "Bua"));
            colors.add(ContextCompat.getColor(this, R.color.admin_chart_green));
        }
        if (houseRent > 0) {
            pieEntries.add(new PieEntry((float) houseRent, "Rent"));
            colors.add(ContextCompat.getColor(this, R.color.admin_chart_red));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
        
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false); // Hide the library legend as we have a custom one
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
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
