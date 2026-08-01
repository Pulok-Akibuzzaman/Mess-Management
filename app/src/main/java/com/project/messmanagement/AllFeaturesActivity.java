package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class AllFeaturesActivity extends AppCompatActivity {

    private RecyclerView rvStats;
    private StatAdapter statAdapter;
    private List<StatItem> statList;

    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;
    private MaterialButton btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_features);

        btn_home = findViewById(R.id.btn_home_layout);
        btn_member = findViewById(R.id.btn_member_layout);
        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);
        btnSignOut = findViewById(R.id.btn_sign_out);

        setupBottomNavigation();
        setupSignOutButton();
        setupFeatureClickListeners();
        setupStatsList();
    }

    private void setupBottomNavigation() {
        btn_home.setOnClickListener(v -> {
            Intent i = new Intent(AllFeaturesActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        btn_member.setOnClickListener(v -> {
            Intent i = new Intent(AllFeaturesActivity.this, MemberActivity.class);
            startActivity(i);
            finish();
        });

        btn_meals.setOnClickListener(v -> {
            Intent i = new Intent(AllFeaturesActivity.this, MealRoutineActivity.class);
            startActivity(i);
            finish();
        });

        btn_bazar.setOnClickListener(v -> {
            Intent i = new Intent(AllFeaturesActivity.this, BazarActivity.class);
            startActivity(i);
            finish();
        });

        btn_cash.setOnClickListener(v -> {
            Intent i = new Intent(AllFeaturesActivity.this, CashLedgerActivity.class);
            startActivity(i);
            finish();
        });

        btn_more.setOnClickListener(v -> {
        });
    }

    private void setupSignOutButton() {
        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> {
                SessionManager sessionManager = new SessionManager(AllFeaturesActivity.this);
                sessionManager.logout();
                Toast.makeText(AllFeaturesActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AllFeaturesActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupFeatureClickListeners() {
        View gridFeatures = findViewById(R.id.grid_features);
        if (gridFeatures != null && gridFeatures instanceof android.widget.GridLayout) {
            android.widget.GridLayout grid = (android.widget.GridLayout) gridFeatures;
            for (int i = 0; i < grid.getChildCount(); i++) {
                View child = grid.getChildAt(i);
                if (child instanceof MaterialCardView) {
                    final int index = i;
                    child.setOnClickListener(v -> handleFeatureClick(index));
                }
            }
        }
    }

    private void handleFeatureClick(int index) {
        Intent intent;
        switch (index) {
            case 0: // Utilities
                intent = new Intent(AllFeaturesActivity.this, UtilityActivity.class);
                break;
            case 1: // Equipment
                intent = new Intent(AllFeaturesActivity.this, EquipmentActivity.class);
                break;
            case 2: // Notices
                intent = new Intent(AllFeaturesActivity.this, NoticesActivity.class);
                break;
            case 3: // Loans
                intent = new Intent(AllFeaturesActivity.this, LoanActivity.class);
                break;
            case 4: // Polls
                intent = new Intent(AllFeaturesActivity.this, PollsActivity.class);
                break;
            case 5: // Complaints
                intent = new Intent(AllFeaturesActivity.this, ComplaintsActivity.class);
                break;
            case 6: // SOS
                intent = new Intent(AllFeaturesActivity.this, SOSActivity.class);
                break;
            case 7: // Room Service
                intent = new Intent(AllFeaturesActivity.this, RoomServiceActivity.class);
                break;
            case 8: // Bua Management
                intent = new Intent(AllFeaturesActivity.this, BuaManagementActivity.class);
                break;
            case 9: // Occasions
                intent = new Intent(AllFeaturesActivity.this, OccasionActivity.class);
                break;
            case 10: // Monthly Report
                intent = new Intent(AllFeaturesActivity.this, MonthlyReportActivity.class);
                break;
            case 11: // Guest Meals
                intent = new Intent(AllFeaturesActivity.this, GuestMealsActivity.class);
                break;
            case 12: // BT Chat
                intent = new Intent(AllFeaturesActivity.this, BTChatActivity.class);
                break;
            default:
                Toast.makeText(this, "Feature not yet implemented", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }

    private void setupStatsList() {
        rvStats = findViewById(R.id.rv_quick_stats);
        rvStats.setLayoutManager(new LinearLayoutManager(this));

        statList = new ArrayList<>();
        statList.add(new StatItem("Bua salary due", "৳4,000", true));
        statList.add(new StatItem("Outstanding loans", "৳3,000", false));
        statList.add(new StatItem("Pending room requests", "2 items", false));
        statList.add(new StatItem("Unread complaints", "2 new", true));

        statAdapter = new StatAdapter(statList);
        rvStats.setAdapter(statAdapter);
    }
}
