package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EquipmentActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        container = findViewById(R.id.item_container); // Update layout if needed

        addEquipment("Kitchen Stove", "Good Condition");
        addEquipment("Dining Table", "Need Repair");
        addEquipment("Water Filter", "Working");

        findViewById(R.id.btnAddEquipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EquipmentActivity.this, "Add Equipment clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEquipment(String name, String status) {
        if (container == null) return;

        TextView tv = new TextView(this);
        tv.setText(name + " - " + status);
        tv.setTextSize(18);
        tv.setPadding(20, 30, 20, 30);
        
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(android.graphics.Color.LTGRAY);

        container.addView(tv);
        container.addView(line);
    }
}
