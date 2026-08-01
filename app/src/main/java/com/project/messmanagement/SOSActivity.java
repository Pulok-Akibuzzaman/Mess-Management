package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SOSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        Button btnEmergency = findViewById(R.id.btnAddNotice);
        if (btnEmergency != null) {
            btnEmergency.setOnClickListener(v -> triggerSOS());
        }
    }

    private void triggerSOS() {
        Toast.makeText(this, "SOS Alert sent!", Toast.LENGTH_SHORT).show();
    }
}
