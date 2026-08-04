package com.project.messmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private MemberAdapter adapter;
    private final List<Member> memberList = new ArrayList<>();
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        dbHelper = new DatabaseHelper(this);

        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemberAdapter(memberList, this::confirmDeleteMember);


        rvMembers.setAdapter(adapter);

        loadMembers(null);

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMembers(s.toString().trim());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        ImageButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> showAddMemberDialog());

        setupNavigation();
    }

    /** Reloads memberList from the database (optionally filtered) and refreshes the adapter. */
    private void loadMembers(String query) {
        memberList.clear();

        Cursor c = (query == null || query.isEmpty())
                ? dbHelper.getAllMembers()
                : dbHelper.searchMembers(query);

        int idxId     = c.getColumnIndexOrThrow("id");
        int idxName   = c.getColumnIndexOrThrow("name");
        int idxRoom   = c.getColumnIndexOrThrow("room");
        int idxStatus = c.getColumnIndexOrThrow("status");

        while (c.moveToNext()) {
            int id        = c.getInt(idxId);
            String name   = c.getString(idxName);
            String room   = c.getString(idxRoom);
            String status = c.getString(idxStatus);

            // NOTE: the "members" table only stores name/room/status, so phone,
            // meals and due amount aren't persisted yet. Defaulted here so the
            // card layout still renders correctly.
            memberList.add(new Member(id, name, initialsOf(name), room, "N/A", 0, "৳0", status));
        }
        c.close();

        adapter.notifyDataSetChanged();
    }

    private String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
            }
        }
        return sb.toString();
    }

    /** Shown on long-press of a member card. */
    private void confirmDeleteMember(Member member) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Member")
                .setMessage("Are you sure you want to delete " + member.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteMember(member.id);
                    loadMembers(etSearch.getText().toString().trim());
                    Toast.makeText(this, member.name + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showAddMemberDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(MemberActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        bottomSheet.setContentView(view);

        EditText etFullName   = view.findViewById(R.id.etFullName);
        EditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Active", "Away"});
        spinnerStatus.setAdapter(statusAdapter);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheet.dismiss());

        view.findViewById(R.id.btnAddMember).setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String room = etRoomNumber.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem() != null
                    ? spinnerStatus.getSelectedItem().toString() : "Active";

            if (name.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "Please enter a name and room number", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.addMember(name, room, status);
            etSearch.setText("");
            loadMembers(null);
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
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