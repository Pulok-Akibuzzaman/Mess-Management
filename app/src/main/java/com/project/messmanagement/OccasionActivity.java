package com.project.messmanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

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
    private boolean isAdmin = false;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occasion);

        db = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = pref.getString("email", "anonymous");
        String role = pref.getString("role", "Member");
        isAdmin = "Admin".equalsIgnoreCase(role);

        initViews();
        setupRecyclerView();
        setupNavigation();
        loadOccasions();

        View fabAdd = findViewById(R.id.fab_add_occasion);
        fabAdd.setOnClickListener(v -> showOccasionDialog(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOccasions();
        fetchOccasionsFromCloud();
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
                if (item.addedBy != null && item.addedBy.equalsIgnoreCase(currentUserEmail)) {
                    showOccasionDialog(item);
                } else {
                    Toast.makeText(OccasionActivity.this, "Only the creator can edit this occasion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(Occasion item) {
                if (item.addedBy != null && item.addedBy.equalsIgnoreCase(currentUserEmail)) {
                    confirmDelete(item);
                } else {
                    Toast.makeText(OccasionActivity.this, "Only the creator can delete this occasion", Toast.LENGTH_SHORT).show();
                }
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
                String addedBy = cursor.getColumnIndex("added_by") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("added_by")) : "System";
                
                occasionList.add(new Occasion(id, title, type, cost, members, date, addedBy));
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
            String date = etDate.getText().toString().trim();

            if (title.isEmpty() || costStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double cost = Double.parseDouble(costStr);
            int members = db.getActiveMemberCount();
            if (members <= 0) members = 1;

            if (isEdit) {
                db.updateOccasion(existing.id, title, type, cost, members, date);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                db.addOccasion(title, type, cost, members, date, currentUserEmail);

                // Sync to Supabase
                String json = "{" +
                        "\"title\": \"" + title + "\"," +
                        "\"type\": \"" + type + "\"," +
                        "\"total_cost\": " + cost + "," +
                        "\"member_count\": " + members + "," +
                        "\"date\": \"" + date + "\"," +
                        "\"added_by\": \"" + currentUserEmail + "\"" +
                        "}";
                RemoteAccess.getInstance().syncToSupabase("occasions", json);

                // Automatically record in Cash Ledger
                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String name = pref.getString("name", "User");
                db.addCashTransaction("Occasion: " + title, cost, "OUT", date, name, "");

                Toast.makeText(this, "Added & Recorded in Ledger", Toast.LENGTH_SHORT).show();
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
                    
                    // Sync Delete to Supabase
                    try {
                        String query = "title=eq." + java.net.URLEncoder.encode(item.title, "UTF-8") +
                                "&date=eq." + java.net.URLEncoder.encode(item.date, "UTF-8");
                        RemoteAccess.getInstance().syncActionToSupabase("occasions", "DELETE", null, query);
                    } catch (Exception ignored) {}

                    loadOccasions();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private void fetchOccasionsFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("occasions", "order=id.desc");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String title = obj.getString("title");
                        String type = obj.getString("type");
                        double cost = obj.getDouble("total_cost");
                        int members = obj.getInt("member_count");
                        String date = obj.getString("date");
                        String addedBy = obj.getString("added_by");

                        if (!occasionExistsLocally(title, date)) {
                            db.addOccasion(title, type, cost, members, date, addedBy);
                        }
                    }
                    runOnUiThread(this::loadOccasions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean occasionExistsLocally(String title, String date) {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id FROM occasions WHERE title=? AND date=?",
                new String[]{title, date});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}
