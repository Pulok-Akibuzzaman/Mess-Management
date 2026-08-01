package com.project.messmanagement;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BTChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        android.widget.Toast.makeText(this, "Bluetooth Chat: Scanning for devices...", android.widget.Toast.LENGTH_SHORT).show();
        
        android.widget.ImageButton btnScan = findViewById(R.id.btnAddNotice);
        if (btnScan != null) {
            btnScan.setOnClickListener(v -> android.widget.Toast.makeText(this, "No other devices found yet.", android.widget.Toast.LENGTH_SHORT).show());
        }
    }
}
