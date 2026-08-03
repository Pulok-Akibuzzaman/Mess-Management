package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PollsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        TextView tvTitle = findViewById(R.id.tvActivityTitle);
        if (tvTitle != null) tvTitle.setText("Polls");

        findViewById(R.id.btnAddNotice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PollsActivity.this, "Create Poll clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
