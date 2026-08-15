package com.project.messmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SOSActivity extends AppCompatActivity {

    private LinearLayout contactContainer;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        db = new DatabaseHelper(this);
        contactContainer = findViewById(R.id.contact_container);

        // 1. SOS Button Click
        findViewById(R.id.card_sos_trigger).setOnClickListener(v -> triggerSOS());
        
        // 2. Add Contact Button
        findViewById(R.id.btn_add_contact).setOnClickListener(v -> showAddContactDialog());

        // Check for Call Permission early
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
        }

        // Check if we were opened by the background alarm (Fallback for removed feature)
        if (getIntent().getBooleanExtra("TRIGGER_DIAL", false)) {
            triggerSOS();
        }

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContactList();
    }

    private void refreshContactList() {
        if (contactContainer == null) return;
        contactContainer.removeAllViews();

        Cursor cursor = db.getAllEmergencyContacts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                addContactToUI(id, name, phone);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void triggerSOS() {
        Cursor cursor = db.getAllEmergencyContacts();
        if (cursor != null && cursor.moveToFirst()) {
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            cursor.close();
            makeDirectCall(phone);
        } else {
            Toast.makeText(this, "No emergency contacts found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeDirectCall(String phone) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, call immediately
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        } else {
            // Permission missing, request it and open dialer as fallback
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
            Toast.makeText(this, "Enable 'Call' permission in settings for automatic dialing", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Automatic calling enabled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddContactDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        dialog.setContentView(view);
        final EditText etName = view.findViewById(R.id.etContactName);
        final EditText etPhone = view.findViewById(R.id.etContactPhone);
        
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            if (!name.isEmpty() && !phone.isEmpty()) {
                db.addEmergencyContact(name, phone);
                refreshContactList();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addContactToUI(final int id, String name, final String phone) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 10, 0, 10);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(name + " (" + phone + ")");
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button btnCall = new Button(this);
        btnCall.setText("Call");
        btnCall.setOnClickListener(v -> makeDirectCall(phone));

        row.addView(tv);
        row.addView(btnCall);
        
        row.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + name + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteEmergencyContact(id);
                    refreshContactList();
                })
                .setNegativeButton("Cancel", null)
                .show();
            return true;
        });

        contactContainer.addView(row);
    }

    private void setupNavigation() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = pref.getString("role", "Member");
        boolean isBuaRole = "Bua".equalsIgnoreCase(role);

        if (isBuaRole) {
            findViewById(R.id.btn_bazar_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_cash_layout).setVisibility(View.GONE);
            findViewById(R.id.btn_meals_layout).setVisibility(View.GONE);
            
            LinearLayout btnSalaryNav = findViewById(R.id.btn_member_layout);
            if (btnSalaryNav != null) {
                ((TextView) btnSalaryNav.getChildAt(1)).setText("Salary");
                ((ImageView) btnSalaryNav.getChildAt(0)).setImageResource(R.drawable.ic_briefcase);
            }
        }

        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        if (findViewById(R.id.btn_member_layout) != null) {
            findViewById(R.id.btn_member_layout).setOnClickListener(v -> {
                if (isBuaRole) {
                    startActivity(new Intent(this, BuaManagementActivity.class));
                } else {
                    startActivity(new Intent(this, MemberActivity.class));
                }
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
