package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GuestMealsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        TextView tvTitle = findViewById(R.id.tvActivityTitle);
        if (tvTitle != null) tvTitle.setText("Guest Meals");

        findViewById(R.id.btnAddNotice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GuestMealsActivity.this, "Add Guest Meal clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
