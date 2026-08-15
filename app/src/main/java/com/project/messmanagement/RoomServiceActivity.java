package com.project.messmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RoomServiceActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private RoomRequestAdapter adapter;
    private List<RoomRequest> requestList = new ArrayList<>();
    private DatabaseHelper db;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_service);

        db = new DatabaseHelper(this);
        
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        isAdmin = "Admin".equalsIgnoreCase(pref.getString("role", "Member"));

        rvRequests = findViewById(R.id.rvRoomRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RoomRequestAdapter(requestList, new RoomRequestAdapter.OnRequestClickListener() {
            @Override
            public void onItemClick(RoomRequest request) {
                if (isAdmin) showStatusDialog(request);
            }

            @Override
            public void onItemLongClick(RoomRequest request) {
                if (isAdmin) confirmDelete(request);
            }
        });
        rvRequests.setAdapter(adapter);

        // 1. Top Right "+" Button -> Full Dialog
        findViewById(R.id.btnAddRequest).setOnClickListener(v -> showRoomRequestDialog());

        // 2. Main Screen "Submit Request" Button -> Quick Request
        final EditText etQuick = findViewById(R.id.etQuickRequest);
        findViewById(R.id.btnSubmitRequest).setOnClickListener(v -> {
            String msg = etQuick.getText().toString().trim();
            if (!msg.isEmpty()) {
                String date = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(Calendar.getInstance().getTime());
                db.addRoomRequest("Quick User", "General", msg, "Medium", date);
                
                // Sync to Supabase
                String json = "{" +
                        "\"member_name\": \"Quick User\"," +
                        "\"room_no\": \"General\"," +
                        "\"issue\": \"" + msg + "\"," +
                        "\"priority\": \"Medium\"," +
                        "\"status\": \"Pending\"," +
                        "\"date\": \"" + date + "\"" +
                        "}";
                RemoteAccess.getInstance().syncToSupabase("room_requests", json);

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
        fetchRequestsFromCloud();
    }

    private void refreshRequestList() {
        requestList.clear();

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

                requestList.add(new RoomRequest(id, member, room, issue, priority, status, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showStatusDialog(RoomRequest request) {
        new AlertDialog.Builder(this)
            .setTitle("Update Status")
            .setItems(new String[]{"Pending", "In Progress", "Completed"}, (d, which) -> {
                String newStatus = (which == 0) ? "Pending" : (which == 1) ? "In Progress" : "Completed";
                db.updateRoomRequestStatus(request.id, newStatus);
                
                // Sync Update to Supabase
                String json = "{\"status\": \"" + newStatus + "\"}";
                String query = "member_name=eq." + request.memberName + "&issue=eq." + request.issue;
                RemoteAccess.getInstance().syncActionToSupabase("room_requests", "PATCH", json, query);
                
                refreshRequestList();
            }).show();
    }

    private void confirmDelete(RoomRequest request) {
        new AlertDialog.Builder(this)
            .setMessage("Remove this request?")
            .setPositiveButton("Delete", (d, w) -> {
                db.deleteRoomRequest(request.id);
                
                // Sync Delete to Supabase
                try {
                    String query = "member_name=eq." + java.net.URLEncoder.encode(request.memberName, "UTF-8") +
                            "&issue=eq." + java.net.URLEncoder.encode(request.issue, "UTF-8") +
                            "&date=eq." + java.net.URLEncoder.encode(request.date, "UTF-8");
                    RemoteAccess.getInstance().syncActionToSupabase("room_requests", "DELETE", null, query);
                } catch (Exception ignored) {}

                refreshRequestList();
            })
            .setNegativeButton("Cancel", null)
            .show();
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
                
                // Sync to Supabase
                String json = "{" +
                        "\"member_name\": \"" + member + "\"," +
                        "\"room_no\": \"" + room + "\"," +
                        "\"issue\": \"" + issue + "\"," +
                        "\"priority\": \"" + prio + "\"," +
                        "\"status\": \"Pending\"," +
                        "\"date\": \"" + date + "\"" +
                        "}";
                RemoteAccess.getInstance().syncToSupabase("room_requests", json);

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

    private void setupNavigation() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sp.getString("role", "Member");
        boolean isBua = "Bua".equalsIgnoreCase(role);

        findViewById(R.id.btn_home_layout).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
            if (isBua) {
                startActivity(new Intent(this, BuaManagementActivity.class));
            } else {
                startActivity(new Intent(this, MemberActivity.class));
            }
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> startActivity(new Intent(this, MealRoutineActivity.class)));
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> startActivity(new Intent(this, BazarActivity.class)));
        findViewById(R.id.btn_cash_layout).setOnClickListener(v -> startActivity(new Intent(this, CashLedgerActivity.class)));
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> startActivity(new Intent(this, AllFeaturesActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fetchRequestsFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("room_requests", "order=id.desc");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String member = obj.getString("member_name");
                        String room = obj.getString("room_no");
                        String issue = obj.getString("issue");
                        String prio = obj.getString("priority");
                        String status = obj.getString("status");
                        String date = obj.getString("date");

                        if (!requestExistsLocally(member, issue, date)) {
                            db.addRoomRequest(member, room, issue, prio, date);
                            // We might need to update status if it changed in cloud
                        }
                    }
                    runOnUiThread(this::refreshRequestList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean requestExistsLocally(String member, String issue, String date) {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id FROM room_requests WHERE member_name=? AND issue=? AND date=?",
                new String[]{member, issue, date});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}
