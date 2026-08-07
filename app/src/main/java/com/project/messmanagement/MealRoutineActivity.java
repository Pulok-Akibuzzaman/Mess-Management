package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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

    private TextView tvB, tvL, tvD;
    private TextView tvTodayDate, tvMyTotal, tvMyTotalCost;
    private LinearLayout routineContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_routine);

        db = new DatabaseHelper(this);
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = pref.getString("email", "anonymous");

        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());

        initViews();
        loadTodayStatus();
        loadMealHistory();
        updateSummary();
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

        tvTodayDate.setText(new SimpleDateFormat("EEEE, dd MMM", Locale.US).format(Calendar.getInstance().getTime()));

        findViewById(R.id.btn_b_plus).setOnClickListener(v -> updateMeal("B", 1));
        findViewById(R.id.btn_b_minus).setOnClickListener(v -> updateMeal("B", -1));
        findViewById(R.id.btn_l_plus).setOnClickListener(v -> updateMeal("L", 1));
        findViewById(R.id.btn_l_minus).setOnClickListener(v -> updateMeal("L", -1));
        findViewById(R.id.btn_d_plus).setOnClickListener(v -> updateMeal("D", 1));
        findViewById(R.id.btn_d_minus).setOnClickListener(v -> updateMeal("D", -1));
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
        loadMealHistory(); // Refresh history list
    }

    private void updateCounts() {
        tvB.setText(String.valueOf(b));
        tvL.setText(String.valueOf(l));
        tvD.setText(String.valueOf(d));
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
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentName = pref.getString("name", "User");
        
        Cursor cursor = db.getUserMealHistory(currentUserEmail, currentName);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);
                int bVal = cursor.getInt(1);
                int lVal = cursor.getInt(2);
                int dVal = cursor.getInt(3);

                // Format: B:1 | L:1 | D:1
                String mealStr = "B:" + bVal + " | L:" + lVal + " | D:" + dVal;
                
                // Prettier date
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
            empty.setText("No history found for the last 30 days.");
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
        TextView d = new TextView(this); d.setText(day); d.setTextColor(Color.BLACK); d.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        TextView m = new TextView(this); m.setText(meal); m.setTextColor(Color.GRAY); m.setGravity(android.view.Gravity.END);
        row.addView(d); row.addView(m);
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
