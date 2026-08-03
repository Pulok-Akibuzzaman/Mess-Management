package com.project.messmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MemberActivity extends AppCompatActivity {

    private LinearLayout memberContainer;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        memberContainer = findViewById(R.id.member_container);
        db = new DatabaseHelper(this);

        // 1. Load members from Database
        refreshMemberList("");

        // 2. Setup Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshMemberList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 3. Setup Add Button
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMemberDialog();
            }
        });

        setupNavigation();
    }

    private void refreshMemberList(String query) {
        if (memberContainer == null) return;
        memberContainer.removeAllViews(); // Clear existing UI

        Cursor cursor;
        if (query == null || query.isEmpty()) {
            cursor = db.getAllMembers();
        } else {
            cursor = db.searchMembers(query);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String room = cursor.getString(cursor.getColumnIndexOrThrow("room"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                
                // Add this member to the UI
                addMemberToUI(id, name, room, status);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showAddMemberDialog() {
        showMemberDialog(-1, "", "", "Active");
    }

    private void showMemberDialog(final int id, String initialName, String initialRoom, String initialStatus) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        dialog.setContentView(view);


        final EditText etName = view.findViewById(R.id.etFullName);
        final EditText etRoom = view.findViewById(R.id.etRoomNumber);
        final EditText etJoinDate = view.findViewById(R.id.etJoinDate);
        final Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        Button btnAction = view.findViewById(R.id.btnAddMember);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        // Pre-fill data for editing
        if (id != -1) {
            btnAction.setText("Update Member");
            etName.setText(initialName);
            etRoom.setText(initialRoom.replace("Room ", ""));
        }

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        etJoinDate.setText(sdf.format(calendar.getTime()));

        etJoinDate.setOnClickListener(v -> {
            new DatePickerDialog(MemberActivity.this, (view1, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                etJoinDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Active", "Away"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        if (id != -1) {
            int spinnerPosition = adapter.getPosition(initialStatus.split(" \\(")[0]);
            spinnerStatus.setSelection(spinnerPosition);
        }

        btnAction.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String room = "Room " + etRoom.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            if (!name.isEmpty()) {
                if (id == -1) {
                    db.addMember(name, room, status);
                    Toast.makeText(MemberActivity.this, "New member added!", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateMember(id, name, room, status);
                    Toast.makeText(MemberActivity.this, "Member updated!", Toast.LENGTH_SHORT).show();
                }
                refreshMemberList("");
                dialog.dismiss();
            } else {
                Toast.makeText(MemberActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addMemberToUI(final int id, final String name, final String room, final String status) {

        // Inflate your custom card layout
        View itemView = getLayoutInflater().inflate(
                R.layout.item_membercard,
                memberContainer,
                false
        );

        // Find views
        TextView tvInitials = itemView.findViewById(R.id.tvInitials);
        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvStatus = itemView.findViewById(R.id.tvStatus);
        TextView tvRoomPhone = itemView.findViewById(R.id.tvRoomPhone);
        TextView tvMeals = itemView.findViewById(R.id.tvMeals);
        TextView tvDue = itemView.findViewById(R.id.tvDue);

        String initials = "";

        if (name != null && !name.trim().isEmpty()) {
            String[] parts = name.trim().split("\\s+");

            initials += parts[0].substring(0, 1);

            if (parts.length > 1) {
                initials += parts[parts.length - 1].substring(0, 1);
            }
        }

        tvInitials.setText(initials.toUpperCase());
        tvName.setText(name);
        tvStatus.setText(status);

        if (status.equalsIgnoreCase("Active")) {
            tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatus.setBackgroundResource(R.drawable.bg_badge_away);
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        tvRoomPhone.setText(room);

        tvMeals.setText("0");
        tvDue.setText("৳0");

        itemView.setOnClickListener(v ->
                showMemberDialog(id, name, room, status)
        );

        itemView.setOnLongClickListener(v -> {

            new AlertDialog.Builder(MemberActivity.this)
                    .setTitle("Delete Member")
                    .setMessage("Are you sure you want to delete " + name + "?")
                    .setPositiveButton("Confirm", (dialog, which) -> {

                        db.deleteMember(id);

                        refreshMemberList("");

                        Toast.makeText(
                                MemberActivity.this,
                                name + " deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        memberContainer.addView(itemView);
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
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
