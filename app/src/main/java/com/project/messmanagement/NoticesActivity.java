package com.project.messmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NoticesActivity extends AppCompatActivity {

    private LinearLayout noticeContainer;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        db = new DatabaseHelper(this);
        noticeContainer = findViewById(R.id.notice_container);

        // 1. Setup Add Button
        findViewById(R.id.btnAddNotice).setOnClickListener(v -> showNoticeDialog(-1, "", "", "Medium", "All Members"));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNoticeList();
    }

    private void refreshNoticeList() {
        if (noticeContainer == null) return;
        noticeContainer.removeAllViews();

        Cursor cursor = db.getAllNotices();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"));
                String audience = cursor.getString(cursor.getColumnIndexOrThrow("audience"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                addNoticeToUI(id, title, content, priority, audience, date);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showNoticeDialog(final int id, String initialTitle, String initialContent, String initialPriority, String initialAudience) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_notice, null);
        dialog.setContentView(view);

        final EditText etTitle = view.findViewById(R.id.etNoticeTitle);
        final EditText etContent = view.findViewById(R.id.etNoticeDetails);
        final Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        final Spinner spinnerAudience = view.findViewById(R.id.spinnerAudience);
        Button btnPost = view.findViewById(R.id.btnPostNotice);
        ImageButton btnClose = view.findViewById(R.id.btnCloseNotice);

        // Priority Spinner Setup
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"High", "Medium", "Low"});
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Audience Spinner Setup
        ArrayAdapter<String> audienceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"All Members", "Admins Only", "Members Only"});
        audienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAudience.setAdapter(audienceAdapter);

        if (id != -1) {
            etTitle.setText(initialTitle);
            etContent.setText(initialContent);
            spinnerPriority.setSelection(priorityAdapter.getPosition(initialPriority));
            spinnerAudience.setSelection(audienceAdapter.getPosition(initialAudience));
            btnPost.setText("Update Notice");
        }

        btnPost.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            String audience = spinnerAudience.getSelectedItem().toString();
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(Calendar.getInstance().getTime());

            if (!title.isEmpty() && !content.isEmpty()) {
                if (id == -1) {
                    db.addNotice(title, content, priority, audience, date);
                    Toast.makeText(this, "Notice posted", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateNotice(id, title, content, priority, audience, date);
                    Toast.makeText(this, "Notice updated", Toast.LENGTH_SHORT).show();
                }
                refreshNoticeList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill Title and Details", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addNoticeToUI(final int id, final String title, final String content, final String priority, final String audience, final String date) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setClickable(true);
        itemLayout.setFocusable(true);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tv = new TextView(this);
        String priorityTag = "[" + priority.toUpperCase() + "]";
        tv.setText(priorityTag + " " + title + "\n" + content + "\n" + date + " | " + audience);
        tv.setTextSize(16);
        tv.setPadding(20, 30, 20, 30);
        
        if (priority.equalsIgnoreCase("High")) tv.setTextColor(Color.RED);
        else if (priority.equalsIgnoreCase("Medium")) tv.setTextColor(Color.BLUE);
        else tv.setTextColor(Color.BLACK);

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.LTGRAY);

        itemLayout.addView(tv);
        itemLayout.addView(line);

        itemLayout.setOnClickListener(v -> showNoticeDialog(id, title, content, priority, audience));

        itemLayout.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Notice")
                    .setMessage("Delete this notice?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        db.deleteNotice(id);
                        refreshNoticeList();
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        noticeContainer.addView(itemLayout);
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
