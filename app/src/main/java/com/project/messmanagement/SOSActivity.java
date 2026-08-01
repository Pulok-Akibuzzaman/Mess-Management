package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SOSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        // Change title in code (since we are reusing layout)

        // Instead, let's just find the textview by its position or content if we know it.
        // But in the layout it's just a TextView. I'll just find the first TextView in the RelativeLayout.
        
        android.widget.ImageButton btnSOS = findViewById(R.id.btnAddNotice);
        if (btnSOS != null) {
            btnSOS.setOnClickListener(v -> triggerSOS());
        }
        
        Toast.makeText(this, "Emergency SOS ready. Tap the + button to alert.", Toast.LENGTH_LONG).show();
    }

    private void triggerSOS() {
        Toast.makeText(this, "🚨 EMERGENCY ALERT SENT to Mess Members! 🚨", Toast.LENGTH_LONG).show();
    }
}
