package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AllFeaturesActivity extends AppCompatActivity {

    LinearLayout btnHome, btnBazar, btnCash, btnMeals;
    private boolean isBua = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_features);

        // 1. Navigation
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isBua = "Bua".equalsIgnoreCase(role);

        btnHome = findViewById(R.id.btn_home_layout);
        if (btnHome != null) {
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AllFeaturesActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }

        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });

        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });

        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });

        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });

        // 2. Feature Clicks
        setupFeature(R.id.card_utilities, UtilityActivity.class);
        setupFeature(R.id.card_equipment, EquipmentActivity.class);
        setupFeature(R.id.card_notices, NoticesActivity.class);
        setupFeature(R.id.card_loans, LoanActivity.class);
        setupFeature(R.id.card_sos, SOSActivity.class);
        setupFeature(R.id.card_room_service, RoomServiceActivity.class);
        setupFeature(R.id.card_bua, BuaManagementActivity.class);
        setupFeature(R.id.card_bt_chat, BTChatActivity.class);
        setupFeature(R.id.card_polls, PollsActivity.class);
        setupFeature(R.id.card_complaints, ComplaintsActivity.class);
        setupFeature(R.id.card_occasions, OccasionActivity.class);
        setupFeature(R.id.card_reports, MonthlyReportActivity.class);
        setupFeature(R.id.card_guest_meals, GuestMealsActivity.class);
        setupFeature(R.id.card_profile, UserProfileActivity.class);

        if (isBua) {
            // Hide navbar items she doesn't need
            findViewById(R.id.btn_bazar_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_cash_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_meals_layout).setVisibility(View.GONE);

            // Repurpose Member button as Salary in Navbar
            LinearLayout btnMemberNav = findViewById(R.id.btn_member_layout);
            if (btnMemberNav != null) {
                btnMemberNav.setVisibility(View.VISIBLE);
                ((android.widget.TextView) btnMemberNav.getChildAt(1)).setText("Salary");
                ((android.widget.ImageView) btnMemberNav.getChildAt(0)).setImageResource(R.drawable.ic_briefcase);
                btnMemberNav.setOnClickListener(v -> {
                    startActivity(new Intent(this, BuaManagementActivity.class));
                    finish();
                });
            }

            // Hide everything in the grid
            View grid = findViewById(R.id.grid_features);
            if (grid != null) grid.setVisibility(View.GONE);
            findViewById(R.id.card_bt_chat).setVisibility(View.GONE);

            // Create a clean container for Bua's allowed features
            LinearLayout buaBox = new LinearLayout(this);
            buaBox.setOrientation(LinearLayout.VERTICAL);
            buaBox.setGravity(android.view.Gravity.CENTER);
            buaBox.setPadding(0, 80, 0, 80);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER);

            // Fetch the card she needs (ONLY SOS)
            View cardSos = findViewById(R.id.card_sos);
            View cardBua = findViewById(R.id.card_bua);
            View cardBt = findViewById(R.id.card_bt_chat);

            if (cardSos != null) {
                // Remove from old parent
                ((android.view.ViewGroup) cardSos.getParent()).removeView(cardSos);
                if (cardBua != null) ((android.view.ViewGroup) cardBua.getParent()).removeView(cardBua);
                if (cardBt != null) ((android.view.ViewGroup) cardBt.getParent()).removeView(cardBt);

                cardSos.setVisibility(View.VISIBLE);
                row.addView(cardSos);

                // Make it look like a uniform square card with labels
                int width = (int) (110 * getResources().getDisplayMetrics().density);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(10, 10, 10, 10);
                cardSos.setLayoutParams(lp);

                buaBox.addView(row);
                // Add this new section into the layout
                android.view.ViewGroup parentLayout = (android.view.ViewGroup) grid.getParent();
                int logoutIndex = parentLayout.indexOfChild(findViewById(R.id.btn_sign_out));
                parentLayout.addView(buaBox, Math.max(0, logoutIndex));
            }
        }

        // 3. Sign Out Button
        findViewById(R.id.btn_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(v);
            }
        });
    }

    private void setupFeature(int id, final Class<?> activityClass) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllFeaturesActivity.this, activityClass);
                    startActivity(intent);
                }
            });
        }
    }

    // This method can be linked to the XML onClick for the Sign Out button
    public void signOut(View view) {
        // Clear login session
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllFeaturesActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
