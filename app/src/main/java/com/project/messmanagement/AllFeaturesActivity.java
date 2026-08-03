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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_features);

        // 1. Navigation
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
