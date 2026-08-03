package com.project.messmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ComplaintsActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices); // Reuse list layout

        db = new DatabaseHelper(this);
        container = findViewById(R.id.notice_container);
        
        TextView tvTitle = findViewById(R.id.tvActivityTitle);
        if (tvTitle != null) tvTitle.setText("Complaints");

        // 1. Setup Add Button
        findViewById(R.id.btnAddNotice).setOnClickListener(v -> showComplaintDialog());

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshComplaintList();
    }

    private void refreshComplaintList() {
        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getAllComplaints();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                addComplaintToUI(id, message, date);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showComplaintDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_complaint, null);
        dialog.setContentView(view);

        final EditText etMessage = view.findViewById(R.id.etComplaintMessage);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        btnSubmit.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(Calendar.getInstance().getTime());

            if (!message.isEmpty()) {
                db.addComplaint(message, date);
                Toast.makeText(this, "Complaint submitted anonymously", Toast.LENGTH_SHORT).show();
                refreshComplaintList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please write your complaint", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addComplaintToUI(final int id, final String message, final String date) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(30, 40, 30, 40);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tvMsg = new TextView(this);
        tvMsg.setText(message);
        tvMsg.setTextSize(16);
        tvMsg.setTextColor(Color.BLACK);
        itemLayout.addView(tvMsg);

        TextView tvDate = new TextView(this);
        tvDate.setText("Submitted on: " + date);
        tvDate.setTextSize(11);
        tvDate.setTextColor(Color.GRAY);
        tvDate.setPadding(0, 8, 0, 0);
        itemLayout.addView(tvDate);

        // Long Press to Delete (Admin feature)
        itemLayout.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Complaint")
                    .setMessage("Remove this complaint entry?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        db.deleteComplaint(id);
                        refreshComplaintList();
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.LTGRAY);

        container.addView(itemLayout);
        container.addView(line);
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}
