package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RoomServiceActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_service);

        container = findViewById(R.id.item_container); // Update layout if needed

        addRequest("Fan Repair", "Room 302", "Pending");
        addRequest("Tap Leaking", "Room 105", "Completed");

        findViewById(R.id.btnAddRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RoomServiceActivity.this, "New Request clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRequest(String title, String room, String status) {
        if (container == null) return;

        TextView tv = new TextView(this);
        tv.setText(title + " | " + room + "\nStatus: " + status);
        tv.setTextSize(18);
        tv.setPadding(20, 30, 20, 30);
        
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(android.graphics.Color.LTGRAY);

        container.addView(tv);
        container.addView(line);
    }
}
