package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NoticesActivity extends AppCompatActivity {

    private RecyclerView rvNotices;
    private NoticeAdapter adapter;
    private List<Notice> noticeList = new ArrayList<>();
    private DatabaseHelper db;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        db = new DatabaseHelper(this);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);
        
        rvNotices = findViewById(R.id.rvNotices);
        rvNotices.setLayoutManager(new LinearLayoutManager(this));
        rvNotices.setVisibility(View.VISIBLE);
        findViewById(R.id.scrollLegacy).setVisibility(View.GONE);
        
        adapter = new NoticeAdapter(noticeList, new NoticeAdapter.OnNoticeClickListener() {
            @Override
            public void onItemClick(Notice notice) {
                if (isAdmin) showNoticeDialog(notice.id, notice.title, notice.content, notice.priority);
            }

            @Override
            public void onItemLongClick(Notice notice) {
                if (isAdmin) confirmDelete(notice);
            }
        });
        rvNotices.setAdapter(adapter);

        // 1. Setup Add Button
        View btnAdd = findViewById(R.id.btnAddNotice);
        if (isAdmin) {
            btnAdd.setOnClickListener(v -> showNoticeDialog(-1, "", "", "Medium"));
        } else {
            btnAdd.setVisibility(View.GONE);
        }

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNoticeList();
    }

    private void refreshNoticeList() {
        noticeList.clear();

        Cursor cursor = db.getAllNotices();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"));
                String audience = cursor.getString(cursor.getColumnIndexOrThrow("audience"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                noticeList.add(new Notice(id, title, content, priority, audience, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void confirmDelete(Notice notice) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Are you sure you want to delete this notice?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteNotice(notice.id);
                    refreshNoticeList();
                    Toast.makeText(this, "Notice deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showNoticeDialog(final int id, String initialTitle, String initialContent, String initialPriority) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_notice, null);
        dialog.setContentView(view);

        final EditText etTitle = view.findViewById(R.id.etNoticeTitle);
        final EditText etContent = view.findViewById(R.id.etNoticeDetails);
        final Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        Button btnPost = view.findViewById(R.id.btnPostNotice);
        ImageButton btnClose = view.findViewById(R.id.btnCloseNotice);

        // Priority Spinner Setup
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"High", "Medium", "Low"});
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        if (id != -1) {
            etTitle.setText(initialTitle);
            etContent.setText(initialContent);
            spinnerPriority.setSelection(priorityAdapter.getPosition(initialPriority));
            btnPost.setText("Update Notice");
        }

        btnPost.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            String audience = "Everyone";
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
