package com.project.messmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.project.messmanagement.repositories.AuthRepository;
import com.project.messmanagement.repositories.BazarRepository;
import com.project.messmanagement.repositories.CashRepository;
import com.project.messmanagement.repositories.MealRepository;
import com.project.messmanagement.utils.CurrencyUtils;
import com.project.messmanagement.utils.DateUtils;
import java.util.ArrayList;

/**
 * MainActivity - Admin Dashboard with Real Database Integration
 * Displays key metrics from SQLite database
 */
public class MainActivityNew extends AppCompatActivity {
    private AuthRepository authRepository;
    private BazarRepository bazarRepository;
    private CashRepository cashRepository;
    private MealRepository mealRepository;
    private MessMateDatabase database;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;
    private BarChart barChartMeals;
    private PieChart pieChartExpenses;
    private TextView tvWelcome, tvTotalAmount, tvBazarSpent, tvMealRate, tvActiveMembers, tvCashBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize authentication
        authRepository = new AuthRepository(this);
        if (!authRepository.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Initialize repositories
        database = MessMateDatabase.getDatabase(this);
        String userEmail = authRepository.getCurrentUserEmail();
        bazarRepository = new BazarRepository(database, userEmail);
        cashRepository = new CashRepository(database, userEmail);
        mealRepository = new MealRepository(database, userEmail);

        initViews();
        setupNavigation();
        loadDashboardData();
    }

    private void initViews() {
        // Text views for dashboard stats
        tvWelcome = findViewById(R.id.tv_welcome);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvBazarSpent = findViewById(R.id.tv_bazar_spent);
        tvMealRate = findViewById(R.id.tv_meal_rate);
        tvActiveMembers = findViewById(R.id.tv_active_members);
        tvCashBalance = findViewById(R.id.tv_cash_balance);

        // Bottom navigation buttons
        btn_home = findViewById(R.id.btn_home_layout);
        btn_member = findViewById(R.id.btn_member_layout);
        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);

        // Charts
        barChartMeals = findViewById(R.id.bar_chart_meals);
        pieChartExpenses = findViewById(R.id.pie_chart_expenses);

        // Logout button
        ImageView btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }

        // Set welcome message
        if (tvWelcome != null) {
            tvWelcome.setText("Welcome, " + authRepository.getCurrentUserName());
        }
    }

    private void loadDashboardData() {
        String currentMonth = DateUtils.getCurrentMonth();

        new Thread(() -> {
            try {
                // Get bazar data
                double bazarTotal = bazarRepository.getBazarTotalByMonth(currentMonth);
                int bazarCount = bazarRepository.getCurrentMonthBazarCount();

                // Get cash data
                double balance = cashRepository.getCurrentMonthBalance();
                double income = cashRepository.getTotalIncomeByMonth(currentMonth);

                // Get meal data
                int mealCount = mealRepository.getMealCountByMonth(currentMonth);

                // Update UI
                runOnUiThread(() -> {
                    if (tvBazarSpent != null) {
                        tvBazarSpent.setText(CurrencyUtils.formatCurrency(bazarTotal) + " (" + bazarCount + " items)");
                    }
                    if (tvCashBalance != null) {
                        tvCashBalance.setText(CurrencyUtils.formatCurrency(balance));
                    }
                    if (tvTotalAmount != null) {
                        tvTotalAmount.setText(CurrencyUtils.formatCurrency(bazarTotal));
                    }
                    if (tvMealRate != null) {
                        double mealRate = mealCount > 0 ? bazarTotal / mealCount : 0;
                        tvMealRate.setText(CurrencyUtils.formatCurrency(mealRate));
                    }
                    if (tvActiveMembers != null) {
                        tvActiveMembers.setText("5"); // Hardcoded, can be dynamic
                    }

                    // Setup charts
                    setupBarChart(mealCount);
                    setupPieChart(bazarTotal);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupNavigation() {
        btn_home.setOnClickListener(v -> {
            // Already on home
        });

        if (btn_member != null) {
            btn_member.setOnClickListener(v -> {
                Intent i = new Intent(MainActivityNew.this, MemberActivity.class);
                startActivity(i);
                finish();
            });
        }

        if (btn_meals != null) {
            btn_meals.setOnClickListener(v -> {
                startActivity(new Intent(MainActivityNew.this, MealRoutineActivity.class));
                finish();
            });
        }

        if (btn_bazar != null) {
            btn_bazar.setOnClickListener(v -> {
                startActivity(new Intent(MainActivityNew.this, BazarActivity.class));
                finish();
            });
        }

        if (btn_cash != null) {
            btn_cash.setOnClickListener(v -> {
                startActivity(new Intent(MainActivityNew.this, CashLedgerActivity.class));
                finish();
            });
        }

        if (btn_more != null) {
            btn_more.setOnClickListener(v -> {
                startActivity(new Intent(MainActivityNew.this, AllFeaturesActivity.class));
                finish();
            });
        }
    }

    private void setupBarChart(int mealCount) {
        if (barChartMeals == null) return;

        ArrayList<BarEntry> entries = new ArrayList<>();
        int[] mealData = {15, 22, 20, 25, 16, 28, 24};
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, mealData[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Meals");
        dataSet.setColor(ContextCompat.getColor(this, R.color.admin_chart_blue));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChartMeals.setData(barData);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        XAxis xAxis = barChartMeals.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChartMeals.getAxisLeft().setDrawGridLines(false);
        barChartMeals.getAxisRight().setEnabled(false);
        barChartMeals.getDescription().setEnabled(false);
        barChartMeals.getLegend().setEnabled(false);
        barChartMeals.animateY(1000);
        barChartMeals.invalidate();
    }

    private void setupPieChart(double bazarTotal) {
        if (pieChartExpenses == null) return;

        ArrayList<PieEntry> entries = new ArrayList<>();
        float bazarExpense = (float) (bazarTotal * 0.68);
        float utilityExpense = (float) (bazarTotal * 0.20);
        float buaExpense = (float) (bazarTotal * 0.09);
        float otherExpense = (float) (bazarTotal * 0.03);

        entries.add(new PieEntry(bazarExpense, "Bazar"));
        entries.add(new PieEntry(utilityExpense, "Utility"));
        entries.add(new PieEntry(buaExpense, "Bua"));
        entries.add(new PieEntry(otherExpense, "Other"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                ContextCompat.getColor(this, R.color.admin_chart_blue),
                ContextCompat.getColor(this, R.color.admin_chart_orange),
                ContextCompat.getColor(this, R.color.admin_chart_green),
                ContextCompat.getColor(this, R.color.admin_chart_red)
        );
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);

        pieChartExpenses.setData(data);
        pieChartExpenses.setHoleRadius(70f);
        pieChartExpenses.setTransparentCircleRadius(75f);
        pieChartExpenses.setDrawHoleEnabled(true);
        pieChartExpenses.setDrawCenterText(false);
        pieChartExpenses.getDescription().setEnabled(false);
        pieChartExpenses.getLegend().setEnabled(false);
        pieChartExpenses.animateY(1000);
        pieChartExpenses.invalidate();
    }

    private void logout() {
        authRepository.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivityNew.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
