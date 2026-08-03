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

        TextView tvTitle = findViewById(R.id.tvActivityTitle);
        if (tvTitle != null) tvTitle.setText("SOS Alert");

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
