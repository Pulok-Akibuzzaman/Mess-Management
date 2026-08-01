package com.project.messmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;
    private BarChart barChartMeals;
    private PieChart pieChartExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        ImageView btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }

        btn_home = findViewById(R.id.btn_home_layout);
        btn_member = findViewById(R.id.btn_member_layout);
        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);

        barChartMeals = findViewById(R.id.bar_chart_meals);
        pieChartExpenses = findViewById(R.id.pie_chart_expenses);

        setupNavigation();
        setupBarChart();
        setupPieChart();
    }

    private void setupNavigation() {
        btn_home.setOnClickListener(v -> {
            // Already on Home
        });

        btn_meals.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MealRoutineActivity.class));
            finish();
        });

        btn_bazar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BazarActivity.class));
            finish();
        });

        btn_cash.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CashLedgerActivity.class));
            finish();
        });

        btn_more.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AllFeaturesActivity.class));
            finish();
        });
        
        if (btn_member != null) {
            btn_member.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, MemberActivity.class));
                finish();
            });
        }
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 15));
        entries.add(new BarEntry(1, 22));
        entries.add(new BarEntry(2, 20));
        entries.add(new BarEntry(3, 25));
        entries.add(new BarEntry(4, 16));
        entries.add(new BarEntry(5, 28));
        entries.add(new BarEntry(6, 24));

        BarDataSet dataSet = new BarDataSet(entries, "Meals");
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

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(8900f, "Bazar"));
        entries.add(new PieEntry(6100f, "Utility"));
        entries.add(new PieEntry(4000f, "Bua"));
        entries.add(new PieEntry(1200f, "Other"));

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
        data.setValueTextSize(0f); // Hide values inside slices

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
        sessionManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
