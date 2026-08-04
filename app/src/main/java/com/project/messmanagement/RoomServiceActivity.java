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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RoomServiceActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_service);

        db = new DatabaseHelper(this);
        container = findViewById(R.id.item_container);

        // 1. Top Right "+" Button -> Full Dialog
        findViewById(R.id.btnAddRequest).setOnClickListener(v -> showRoomRequestDialog());

        // 2. Main Screen "Submit Request" Button -> Quick Request
        final EditText etQuick = findViewById(R.id.etQuickRequest);
        findViewById(R.id.btnSubmitRequest).setOnClickListener(v -> {
            String msg = etQuick.getText().toString().trim();
            if (!msg.isEmpty()) {
                String date = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(Calendar.getInstance().getTime());
                db.addRoomRequest("Quick User", "General", msg, "Medium", date);
                etQuick.setText("");
                refreshRequestList();
                Toast.makeText(this, "Quick request submitted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please type something first", Toast.LENGTH_SHORT).show();
            }
        });

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRequestList();
    }

    private void refreshRequestList() {
        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getAllRoomRequests();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String member = cursor.getString(cursor.getColumnIndexOrThrow("member_name"));
                String room = cursor.getString(cursor.getColumnIndexOrThrow("room_no"));
                String issue = cursor.getString(cursor.getColumnIndexOrThrow("issue"));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                addRequestToUI(id, member, room, issue, priority, status, date);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showRoomRequestDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_room_request, null);
        dialog.setContentView(view);

        final Spinner spinnerMember = view.findViewById(R.id.spinnerMember);
        final Spinner spinnerRoom = view.findViewById(R.id.spinnerRoom);
        final EditText etIssue = view.findViewById(R.id.etIssueDescription);
        final Spinner spinnerPriority = view.findViewById(R.id.spinnerRequestPriority);
        Button btnSubmit = view.findViewById(R.id.btnSubmitNewRequest);
        ImageButton btnClose = view.findViewById(R.id.btnCloseRoomRequest);

        // Member Spinner
        List<String> memberList = new ArrayList<>();
        Cursor mCursor = db.getAllMembers();
        if (mCursor != null && mCursor.moveToFirst()) {
            do { memberList.add(mCursor.getString(mCursor.getColumnIndexOrThrow("name"))); } while (mCursor.moveToNext());
            mCursor.close();
        }
        if (memberList.isEmpty()) memberList.add("Guest");
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMember.setAdapter(mAdapter);

        // Room Spinner
        String[] rooms = {"Room 101", "Room 102", "Room 201", "Room 202", "Room 301", "Room 302"};
        ArrayAdapter<String> rAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rooms);
        rAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(rAdapter);

        // Priority Spinner
        String[] priorities = {"Low", "Medium", "High", "Emergency"};
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(pAdapter);

        btnSubmit.setOnClickListener(v -> {
            String member = spinnerMember.getSelectedItem().toString();
            String room = spinnerRoom.getSelectedItem().toString();
            String issue = etIssue.getText().toString().trim();
            String prio = spinnerPriority.getSelectedItem().toString();
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(Calendar.getInstance().getTime());

            if (!issue.isEmpty()) {
                db.addRoomRequest(member, room, issue, prio, date);
                refreshRequestList();
                dialog.dismiss();
                Toast.makeText(this, "Request added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please describe the issue", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addRequestToUI(final int id, String member, String room, String issue, String priority, final String status, String date) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(30, 40, 30, 40);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(issue + " (" + priority + ")");
        tvTitle.setTextSize(17);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(priority.equals("Emergency") ? Color.RED : Color.BLACK);
        itemLayout.addView(tvTitle);

        TextView tvDetails = new TextView(this);
        tvDetails.setText(member + " | " + room + "\nStatus: " + status + " | " + date);
        tvDetails.setTextSize(14);
        tvDetails.setTextColor(Color.GRAY);
        tvDetails.setPadding(0, 5, 0, 0);
        itemLayout.addView(tvDetails);

        // Click to change status
        itemLayout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Update Status")
                .setItems(new String[]{"Pending", "In Progress", "Completed"}, (d, which) -> {
                    String newStatus = (which == 0) ? "Pending" : (which == 1) ? "In Progress" : "Completed";
                    db.updateRoomRequestStatus(id, newStatus);
                    refreshRequestList();
                }).show();
        });

        // Long Press to Delete
        itemLayout.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                .setMessage("Remove this request?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteRoomRequest(id);
                    refreshRequestList();
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
        if (findViewById(R.id.btn_home_layout) != null) {
            findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_member_layout) != null) {
            findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, MemberActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_meals_layout) != null) {
            findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, MealRoutineActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_bazar_layout) != null) {
            findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, BazarActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_cash_layout) != null) {
            findViewById(R.id.btn_cash_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, CashLedgerActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btn_more_layout) != null) {
            findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
                startActivity(new Intent(this, AllFeaturesActivity.class));
                finish();
            });
        }
    }
}
