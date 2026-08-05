package com.project.messmanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OccasionActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView rvOccasions;
    private OccasionAdapter adapter;
    private List<Occasion> occasionList = new ArrayList<>();
    private TextView tvTotalSpent, tvEventCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occasion);

        db = new DatabaseHelper(this);
        initViews();
        setupRecyclerView();
        setupNavigation();
        loadOccasions();

        findViewById(R.id.fab_add_occasion).setOnClickListener(v -> showOccasionDialog(null));
    }

    private void initViews() {
        tvTotalSpent = findViewById(R.id.tvTotalOccasionSpent);
        tvEventCount = findViewById(R.id.tvEventCount);
        rvOccasions = findViewById(R.id.rvOccasions);
    }

    private void setupRecyclerView() {
        rvOccasions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OccasionAdapter(occasionList, new OccasionAdapter.OnOccasionClickListener() {
            @Override
            public void onItemClick(Occasion item) {
                showOccasionDialog(item);
            }

            @Override
            public void onLongClick(Occasion item) {
                confirmDelete(item);
            }
        });
        rvOccasions.setAdapter(adapter);
    }

    private void loadOccasions() {
        occasionList.clear();
        Cursor cursor = db.getAllOccasions();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double cost = cursor.getDouble(cursor.getColumnIndexOrThrow("total_cost"));
                int members = cursor.getInt(cursor.getColumnIndexOrThrow("member_count"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                occasionList.add(new Occasion(id, title, type, cost, members, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();

        double total = db.getTotalOccasionCost();
        tvTotalSpent.setText(String.format(Locale.US, "৳%,.0f", total));
        tvEventCount.setText(String.format(Locale.US, "%d events recorded", occasionList.size()));
    }

    private void showOccasionDialog(Occasion existing) {
        boolean isEdit = existing != null;
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_occasion, null);
        dialog.setContentView(view);

        EditText etTitle = view.findViewById(R.id.etOccasionTitle);
        Spinner spinnerType = view.findViewById(R.id.spinnerType);
        EditText etCost = view.findViewById(R.id.etTotalCost);
        EditText etMembers = view.findViewById(R.id.etMemberCount);
        EditText etDate = view.findViewById(R.id.etDate);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        // Setup Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, 
                new String[]{"Festival", "Social", "Birthday", "Other"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        if (isEdit) {
            etTitle.setText(existing.title);
            spinnerType.setSelection(typeAdapter.getPosition(existing.type));
            etCost.setText(String.valueOf(existing.totalCost));
            etMembers.setText(String.valueOf(existing.memberCount));
            etDate.setText(existing.date);
            btnSave.setText("Update Occasion");
        }

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                Calendar picked = Calendar.getInstance();
                picked.set(year, month, dayOfMonth);
                etDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(picked.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String costStr = etCost.getText().toString().trim();
            String memStr = etMembers.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (title.isEmpty() || costStr.isEmpty() || memStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double cost = Double.parseDouble(costStr);
            int members = Integer.parseInt(memStr);

            if (isEdit) {
                db.updateOccasion(existing.id, title, type, cost, members, date);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                db.addOccasion(title, type, cost, members, date);
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
            }
            loadOccasions();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void confirmDelete(Occasion item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Occasion")
                .setMessage("Delete " + item.title + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteOccasion(item.id);
                    loadOccasions();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_members).setOnClickListener(v -> {
            startActivity(new Intent(this, MemberActivity.class));
            finish();
        });
        findViewById(R.id.btn_meals).setOnClickListener(v -> {
            startActivity(new Intent(this, MealRoutineActivity.class));
            finish();
        });
        findViewById(R.id.btn_bazar).setOnClickListener(v -> {
            startActivity(new Intent(this, BazarActivity.class));
            finish();
        });
        findViewById(R.id.btn_cash).setOnClickListener(v -> {
            startActivity(new Intent(this, CashLedgerActivity.class));
            finish();
        });
        findViewById(R.id.btn_more).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}
