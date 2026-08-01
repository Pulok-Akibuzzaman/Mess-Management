package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OccasionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occasion);

        View fabAdd = findViewById(R.id.fab_add_occasion);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(OccasionActivity.this, "Add Occasion clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
