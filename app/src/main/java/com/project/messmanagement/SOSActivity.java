package com.project.messmanagement;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SOSActivity extends AppCompatActivity {

    private LinearLayout contactContainer;
    private DatabaseHelper db;
    private TextView tvLastCheckin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        db = new DatabaseHelper(this);
        contactContainer = findViewById(R.id.contact_container);
        tvLastCheckin = findViewById(R.id.tv_last_checkin);

        // Update Last Activity on load
        updateLastActivity();
        loadLastCheckinText();

        // 1. SOS Button Click (Action Dial)
        findViewById(R.id.card_sos_trigger).setOnClickListener(v -> triggerSOS());
        
        // 2. Add Contact Button
        findViewById(R.id.btn_add_contact).setOnClickListener(v -> showAddContactDialog());

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContactList();
        loadLastCheckinText();
    }

    private void updateLastActivity() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        pref.edit().putLong("last_activity_time", System.currentTimeMillis()).apply();
    }

    private void loadLastCheckinText() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long lastTime = pref.getLong("last_activity_time", System.currentTimeMillis());
        long diff = System.currentTimeMillis() - lastTime;
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        
        if (hours == 0) tvLastCheckin.setText("Last Activity: Just now");
        else tvLastCheckin.setText("Last Activity: " + hours + "h ago");
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
            
            // ACTION_DIAL pre-fills the number
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
            Toast.makeText(this, "Opening Dialer...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please add an Emergency Contact first", Toast.LENGTH_LONG).show();
        }
    }

    private void showAddContactDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        dialog.setContentView(view);

        final EditText etName = view.findViewById(R.id.etContactName);
        final EditText etPhone = view.findViewById(R.id.etContactPhone);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                db.addEmergencyContact(name, phone);
                refreshContactList();
                dialog.dismiss();
                Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
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
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        row.addView(tv);
        row.addView(btnCall);

        row.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Remove " + name + "?")
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
        findViewById(R.id.btn_home_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.btn_more_layout).setOnClickListener(v -> {
            startActivity(new Intent(this, AllFeaturesActivity.class));
            finish();
        });
    }
}
