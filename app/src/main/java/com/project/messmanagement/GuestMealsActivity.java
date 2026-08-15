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

public class GuestMealsActivity extends AppCompatActivity {

    private RecyclerView rvGuestMeals;
    private GuestMealsAdapter adapter;
    private List<GuestMeal> guestMealList = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_meals);

        db = new DatabaseHelper(this);
        rvGuestMeals = findViewById(R.id.rvGuestMeals);
        rvGuestMeals.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GuestMealsAdapter(guestMealList, new GuestMealsAdapter.OnGuestMealClickListener() {
            @Override
            public void onItemClick(GuestMeal item) {
                showGuestMealDialog(item);
            }

            @Override
            public void onLongClick(GuestMeal item) {
                confirmDelete(item);
            }
        });
        rvGuestMeals.setAdapter(adapter);

        findViewById(R.id.btnAddGuestMeal).setOnClickListener(v -> showGuestMealDialog(null));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGuestMeals();
        fetchGuestMealsFromCloud();
    }

    private void loadGuestMeals() {
        guestMealList.clear();
        Cursor cursor = db.getAllGuestMeals();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String member = cursor.getString(cursor.getColumnIndexOrThrow("member_name"));
                String guest = cursor.getString(cursor.getColumnIndexOrThrow("guest_name"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("meal_count"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("meal_type"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                guestMealList.add(new GuestMeal(id, member, guest, count, type, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showGuestMealDialog(GuestMeal existingItem) {
        boolean isEdit = existingItem != null;
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_guest_meal, null);
        dialog.setContentView(view);

        Spinner spinnerHost = view.findViewById(R.id.spinnerHost);
        EditText etGuestName = view.findViewById(R.id.etGuestName);
        EditText etMealCount = view.findViewById(R.id.etMealCount);
        Spinner spinnerType = view.findViewById(R.id.spinnerMealType);
        EditText etDate = view.findViewById(R.id.etDate);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        // Setup Host Spinner
        List<String> memberNames = new ArrayList<>();
        Cursor memberCursor = db.getAllMembers();
        if (memberCursor != null && memberCursor.moveToFirst()) {
            do {
                memberNames.add(memberCursor.getString(memberCursor.getColumnIndexOrThrow("name")));
            } while (memberCursor.moveToNext());
            memberCursor.close();
        }
        ArrayAdapter<String> hostAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberNames);
        hostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHost.setAdapter(hostAdapter);

        // Setup Meal Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Breakfast", "Lunch", "Dinner"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        if (isEdit) {
            spinnerHost.setSelection(hostAdapter.getPosition(existingItem.memberName));
            etGuestName.setText(existingItem.guestName);
            etMealCount.setText(String.valueOf(existingItem.mealCount));
            spinnerType.setSelection(typeAdapter.getPosition(existingItem.mealType));
            etDate.setText(existingItem.date);
            btnSave.setText("Update Guest Meal");
        }

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                Calendar picked = Calendar.getInstance();
                picked.set(year, month, dayOfMonth);
                etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(picked.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            String host = spinnerHost.getSelectedItem() != null ? spinnerHost.getSelectedItem().toString() : "";
            String guest = etGuestName.getText().toString().trim();
            String countStr = etMealCount.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String date = etDate.getText().toString().trim();

            if (host.isEmpty() || guest.isEmpty() || countStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = Integer.parseInt(countStr);
            if (isEdit) {
                db.updateGuestMeal(existingItem.id, host, guest, count, type, date);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                db.addGuestMeal(host, guest, count, type, date);
                
                // Sync to Supabase
                String json = "{" +
                        "\"member_name\": \"" + host + "\"," +
                        "\"guest_name\": \"" + guest + "\"," +
                        "\"meal_count\": " + count + "," +
                        "\"meal_type\": \"" + type + "\"," +
                        "\"date\": \"" + date + "\"" +
                        "}";
                RemoteAccess.getInstance().syncToSupabase("guest_meals", json);

                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
            }
            loadGuestMeals();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void confirmDelete(GuestMeal item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Guest Meal")
                .setMessage("Are you sure you want to delete this record for " + item.guestName + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteGuestMeal(item.id);
                    
                    // Sync Delete to Supabase
                    try {
                        String query = "member_name=eq." + java.net.URLEncoder.encode(item.memberName, "UTF-8") +
                                "&guest_name=eq." + java.net.URLEncoder.encode(item.guestName, "UTF-8") +
                                "&date=eq." + java.net.URLEncoder.encode(item.date, "UTF-8");
                        RemoteAccess.getInstance().syncActionToSupabase("guest_meals", "DELETE", null, query);
                    } catch (Exception ignored) {}

                    loadGuestMeals();
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

    private void fetchGuestMealsFromCloud() {
        new Thread(() -> {
            String response = RemoteAccess.getInstance().syncFromSupabase("guest_meals", "order=id.desc");
            if (response != null && !response.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String member = obj.getString("member_name");
                        String guest = obj.getString("guest_name");
                        int count = obj.getInt("meal_count");
                        String type = obj.getString("meal_type");
                        String date = obj.getString("date");

                        if (!guestMealExistsLocally(member, guest, date)) {
                            db.addGuestMeal(member, guest, count, type, date);
                        }
                    }
                    runOnUiThread(this::loadGuestMeals);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean guestMealExistsLocally(String member, String guest, String date) {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id FROM guest_meals WHERE member_name=? AND guest_name=? AND date=?",
                new String[]{member, guest, date});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}
