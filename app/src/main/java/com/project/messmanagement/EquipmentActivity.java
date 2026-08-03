package com.project.messmanagement;

import android.app.DatePickerDialog;
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

public class EquipmentActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        db = new DatabaseHelper(this);
        container = findViewById(R.id.item_container);

        // 1. Setup Add Button
        findViewById(R.id.btnAddEquipment).setOnClickListener(v -> showEquipmentDialog(-1, "", "", "", "", 0));

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEquipmentList();
    }

    private void refreshEquipmentList() {
        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getAllEquipment();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("purchase_date"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                addEquipmentToUI(id, name, location, status, date, price);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showEquipmentDialog(final int id, String initialName, String initialLocation, String initialStatus, String initialDate, double initialPrice) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_equipment, null);
        dialog.setContentView(view);


        final EditText etName = view.findViewById(R.id.etEquipmentName);
        final Spinner spinnerLocation = view.findViewById(R.id.spinnerLocation);
        final Spinner spinnerStatus = view.findViewById(R.id.spinnerEquipmentStatus);
        final EditText etDate = view.findViewById(R.id.etPurchaseDate);
        final EditText etPrice = view.findViewById(R.id.etPurchasePrice);
        Button btnSave = view.findViewById(R.id.btnAddEquipmentSubmit);
        ImageButton btnClose = view.findViewById(R.id.btnCloseEquipment);

        // Location Spinner Setup
        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Kitchen", "Dining Room", "Hallway", "Bed Room"});
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locAdapter);

        // Status Spinner Setup
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Good Condition", "Need Repair", "Broken", "Working"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        if (id != -1) {
            etName.setText(initialName);
            etPrice.setText(String.valueOf(initialPrice));
            etDate.setText(initialDate);
            spinnerLocation.setSelection(locAdapter.getPosition(initialLocation));
            spinnerStatus.setSelection(statusAdapter.getPosition(initialStatus));
            btnSave.setText("Update Equipment");
        } else {
            etDate.setText(sdf.format(calendar.getTime()));
        }

        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view1, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                etDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String location = spinnerLocation.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();
            String date = etDate.getText().toString();
            String priceStr = etPrice.getText().toString().trim();

            if (!name.isEmpty() && !priceStr.isEmpty()) {
                double price = Double.parseDouble(priceStr);
                if (id == -1) {
                    db.addEquipment(name, location, status, date, price);
                    Toast.makeText(this, "Equipment added", Toast.LENGTH_SHORT).show();
                } else {
                    db.updateEquipment(id, name, location, status, date, price);
                    Toast.makeText(this, "Equipment updated", Toast.LENGTH_SHORT).show();
                }
                refreshEquipmentList();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addEquipmentToUI(final int id, final String name, final String location, final String status, final String date, final double price) {
        final LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setClickable(true);
        itemLayout.setFocusable(true);
        itemLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        TextView tv = new TextView(this);
        tv.setText(name + " (" + location + ")\n" + status + " | ৳" + (int) price);
        tv.setTextSize(16);
        tv.setPadding(20, 30, 20, 30);
        tv.setTextColor(getResources().getColor(R.color.admin_text_dark));

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.LTGRAY);

        itemLayout.addView(tv);
        itemLayout.addView(line);

        itemLayout.setOnClickListener(v -> showEquipmentDialog(id, name, location, status, date, price));

        itemLayout.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Equipment")
                    .setMessage("Delete " + name + "?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        db.deleteEquipment(id);
                        refreshEquipmentList();
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        container.addView(itemLayout);
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
