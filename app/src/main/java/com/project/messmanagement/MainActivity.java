package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFeatures;
    private FeatureAdapter featureAdapter;
    private List<FeatureItem> featureList;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        ImageView btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }

        setupFeaturesGrid();
    }

    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupFeaturesGrid() {
        rvFeatures = findViewById(R.id.rv_features_grid);
        rvFeatures.setLayoutManager(new GridLayoutManager(this, 3));

        featureList = new ArrayList<>();
        featureList.add(new FeatureItem("Home", R.drawable.ic_home, R.drawable.icon_circle_orange));
        featureList.add(new FeatureItem("Members", R.drawable.ic_members, R.drawable.icon_circle_purple));
        featureList.add(new FeatureItem("Meals", R.drawable.ic_meals, R.drawable.icon_circle_teal));
        featureList.add(new FeatureItem("Bazar", R.drawable.ic_bazar, R.drawable.icon_circle_blue));
        featureList.add(new FeatureItem("Cash", R.drawable.ic_cash, R.drawable.icon_circle_green));
        featureList.add(new FeatureItem("More", R.drawable.ic_more, R.drawable.icon_circle_red));

        featureAdapter = new FeatureAdapter(featureList, this::onFeatureClick);
        rvFeatures.setAdapter(featureAdapter);
    }

    private void onFeatureClick(String featureName) {
        Intent intent = null;
        switch (featureName) {
            case "Bazar":
                intent = new Intent(MainActivity.this, BazarActivity.class);
                break;
            case "Cash":
                intent = new Intent(MainActivity.this, CashLedgerActivity.class);
                break;
            case "Meals":
                intent = new Intent(MainActivity.this, MealRoutineActivity.class);
                break;
            case "More":
                intent = new Intent(MainActivity.this, AllFeaturesActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
