package com.project.messmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyReportActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextView tvGrandTotal, tvTotalMeals, tvMealRate, tvMemberCount;
    private TextView tvBazarCost, tvUtilityCost, tvBuaCost;
    private LinearProgressIndicator pbBazar, pbUtility, pbBua;
    private LinearLayout memberContainer;
    private TextView tvReportMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        db = new DatabaseHelper(this);
        initViews();
        setupNavigation();
        loadReportData();
    }

    private void initViews() {
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvTotalMeals = findViewById(R.id.tvTotalMeals);
        tvMealRate = findViewById(R.id.tvMealRate);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        tvBazarCost = findViewById(R.id.tvBazarCost);
        tvUtilityCost = findViewById(R.id.tvUtilityCost);
        tvBuaCost = findViewById(R.id.tvBuaCost);
        pbBazar = findViewById(R.id.pbBazar);
        pbUtility = findViewById(R.id.pbUtility);
        pbBua = findViewById(R.id.pbBua);
        memberContainer = findViewById(R.id.memberBreakdownContainer);
        tvReportMonth = findViewById(R.id.tvReportMonth);

        String currentMonth = new SimpleDateFormat("MMMM yyyy", Locale.US).format(Calendar.getInstance().getTime());
        tvReportMonth.setText(currentMonth.toUpperCase() + " — GRAND TOTAL");
    }

    private void loadReportData() {
        double totalBazar = db.getTotalBazar();
        double totalUtilities = db.getUtilitiesTotal();
        int totalMeals = db.getTotalMeals();
        int memberCount = db.getActiveMembersCount();
        
        double buaSalary = 0;
        Cursor buaCursor = db.getBuaProfile();
        if (buaCursor != null && buaCursor.moveToFirst()) {
            buaSalary = buaCursor.getDouble(buaCursor.getColumnIndexOrThrow("salary"));
            buaCursor.close();
        }

        double grandTotal = totalBazar + totalUtilities + buaSalary;
        double mealRate = totalMeals > 0 ? totalBazar / totalMeals : 0;
        double sharedCostPerMember = memberCount > 0 ? (totalUtilities + buaSalary) / memberCount : 0;

        // Update Main Card
        tvGrandTotal.setText(String.format(Locale.US, "৳%,.0f", grandTotal));
        tvTotalMeals.setText(String.valueOf(totalMeals));
        tvMealRate.setText(String.format(Locale.US, "৳%,.1f", mealRate));
        tvMemberCount.setText(String.valueOf(memberCount));

        // Update Breakdown
        tvBazarCost.setText(String.format(Locale.US, "৳%,.0f", totalBazar));
        tvUtilityCost.setText(String.format(Locale.US, "৳%,.0f", totalUtilities));
        tvBuaCost.setText(String.format(Locale.US, "৳%,.0f", buaSalary));

        if (grandTotal > 0) {
            pbBazar.setProgress((int) ((totalBazar / grandTotal) * 100));
            pbUtility.setProgress((int) ((totalUtilities / grandTotal) * 100));
            pbBua.setProgress((int) ((buaSalary / grandTotal) * 100));
        }

        // Member Breakdown
        memberContainer.removeAllViews();
        // Add Title back
        TextView title = new TextView(this);
        title.setText("PER MEMBER BREAKDOWN");
        title.setTextSize(13);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.parseColor("#1E293B"));
        title.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 40);
        title.setLayoutParams(lp);
        memberContainer.addView(title);

        Cursor cursor = db.getAllMembers();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                int userMeals = (email != null) ? db.getUserTotalMeals(email) : 0;
                double foodCost = userMeals * mealRate;
                double totalOwed = foodCost + sharedCostPerMember;

                addMemberToUI(name, userMeals, mealRate, foodCost, sharedCostPerMember, totalOwed);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void addMemberToUI(String name, int meals, double rate, double foodCost, double shared, double total) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_member_report, memberContainer, false);
        
        TextView tvInitial = view.findViewById(R.id.tvInitial);
        TextView tvName = view.findViewById(R.id.tvMemberName);
        TextView tvTotalOwed = view.findViewById(R.id.tvTotalOwed);
        TextView tvMealCalc = view.findViewById(R.id.tvMealCalc);
        TextView tvSharedCost = view.findViewById(R.id.tvSharedCost);

        StringBuilder initial = new StringBuilder();
        String[] parts = name.split(" ");
        for (String p : parts) if (!p.isEmpty()) initial.append(p.charAt(0));
        tvInitial.setText(initial.toString().toUpperCase());
        
        tvName.setText(name);
        tvTotalOwed.setText(String.format(Locale.US, "৳%,.0f", total));
        tvMealCalc.setText(String.format(Locale.US, "Meals: %d × ৳%,.1f = ৳%,.0f", meals, rate, foodCost));
        tvSharedCost.setText(String.format(Locale.US, "Shared cost: ৳%,.0f", shared));

        memberContainer.addView(view);

        // Add Divider
        View divider = new View(this);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dlp.setMargins(0, 30, 0, 30);
        divider.setLayoutParams(dlp);
        divider.setBackgroundColor(Color.parseColor("#CBD5E1"));
        memberContainer.addView(divider);
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_members).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_cash).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_more).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}
