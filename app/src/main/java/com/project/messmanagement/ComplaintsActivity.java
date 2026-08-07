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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ComplaintsActivity extends AppCompatActivity {

    private RecyclerView rvComplaints;
    private ComplaintAdapter adapter;
    private List<Complaint> complaintList = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices); // Reuse list layout

        db = new DatabaseHelper(this);
        rvComplaints = findViewById(R.id.rvNotices); // Reuse layout component
        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        rvComplaints.setVisibility(View.VISIBLE);
        findViewById(R.id.scrollLegacy).setVisibility(View.GONE);

        adapter = new ComplaintAdapter(complaintList, complaint -> confirmDelete(complaint));
        rvComplaints.setAdapter(adapter);
        
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
        complaintList.clear();

        Cursor cursor = db.getAllComplaints();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                complaintList.add(new Complaint(id, message, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void confirmDelete(Complaint complaint) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Complaint")
                .setMessage("Remove this complaint entry?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    db.deleteComplaint(complaint.id);
                    refreshComplaintList();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
