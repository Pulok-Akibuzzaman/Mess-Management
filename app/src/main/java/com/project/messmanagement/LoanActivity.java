package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoanActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        container = findViewById(R.id.notice_container);

        addLoan("Loan from Pulok", "1500 TK", "Pending");
        addLoan("Loan for Bazar", "2000 TK", "Urgent");

        findViewById(R.id.btnAddNotice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoanActivity.this, "Add Loan clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLoan(String name, String amount, String status) {
        if (container == null) return;

        TextView tv = new TextView(this);
        tv.setText(name + " | " + amount + "\nStatus: " + status);
        tv.setTextSize(18);
        tv.setPadding(20, 30, 20, 30);
        
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(android.graphics.Color.LTGRAY);

        container.addView(tv);
        container.addView(line);
    }
}
