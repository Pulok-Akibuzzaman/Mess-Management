package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MealRoutineActivity extends AppCompatActivity {


    private LinearLayout btn_home, btn_member, btn_meals, btn_bazar, btn_cash, btn_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_routine);
        
        // Activity logic for managing meal statuses can be added here

        btn_home = findViewById(R.id.btn_home_layout); // pending
        btn_member = findViewById(R.id.btn_member_layout); //pending

        btn_meals = findViewById(R.id.btn_meals_layout);
        btn_bazar = findViewById(R.id.btn_bazar_layout);
        btn_cash = findViewById(R.id.btn_cash_layout);
        btn_more = findViewById(R.id.btn_more_layout);

        btn_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_bazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MealRoutineActivity.this, BazarActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MealRoutineActivity.this, CashLedgerActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MealRoutineActivity.this, AllFeaturesActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}