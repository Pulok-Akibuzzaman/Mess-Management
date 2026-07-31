package com.project.messmanagement;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AllFeaturesActivity extends AppCompatActivity {

    private RecyclerView rvStats;
    private StatAdapter statAdapter;
    private List<StatItem> statList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_features);

        // The feature grid is now statically defined in XML for perfect visual fidelity
        // in the layout editor. Individual click listeners can be added here.

        setupStatsList();
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