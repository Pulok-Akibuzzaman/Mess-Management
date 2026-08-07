package com.project.messmanagement;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView rvFields;
    private ProfileAdapter adapter;
    private List<ProfileField> fieldList = new ArrayList<>();
    private String userEmail, userPhone, userPassword, userName, userRole;
    private TextView tvInitials, tvName, tvRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = new DatabaseHelper(this);
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = pref.getString("email", "").trim().toLowerCase();

        tvInitials = findViewById(R.id.tvProfileInitials);
        tvName = findViewById(R.id.tvProfileName);
        tvRole = findViewById(R.id.tvProfileRole);
        rvFields = findViewById(R.id.rvProfileFields);

        rvFields.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProfileAdapter(fieldList, field -> {
            if (field.label.equals("Phone Number")) showEditDialog("Phone Number", userPhone, true);
            else if (field.label.equals("Password")) showEditDialog("Password", userPassword, false);
        });
        rvFields.setAdapter(adapter);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadUserProfile();
    }

    private void loadUserProfile() {
        Cursor c = db.getMemberByEmail(userEmail);
        if (c != null && c.moveToFirst()) {
            userName = c.getString(c.getColumnIndexOrThrow("name"));
            userPhone = c.getString(c.getColumnIndexOrThrow("phone"));
            userPassword = c.getString(c.getColumnIndexOrThrow("password"));
            userRole = c.getString(c.getColumnIndexOrThrow("status"));
            String room = c.getString(c.getColumnIndexOrThrow("room"));
            String joined = c.getString(c.getColumnIndexOrThrow("join_date"));
            c.close();

            tvName.setText(userName);
            tvRole.setText(userRole + " Member");
            tvInitials.setText(initialsOf(userName));

            fieldList.clear();
            fieldList.add(new ProfileField("Email Address", userEmail, R.drawable.ic_bell, false));
            fieldList.add(new ProfileField("Phone Number", userPhone, R.drawable.ic_members, true));
            fieldList.add(new ProfileField("Password", "••••••••", R.drawable.ic_logout, true));
            fieldList.add(new ProfileField("Room Number", room, R.drawable.ic_home, false));
            fieldList.add(new ProfileField("Joined On", joined, R.drawable.ic_document, false));
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Profile data not found in database", Toast.LENGTH_LONG).show();
            tvName.setText("Not Found");
        }
    }

    private String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }

    private void showEditDialog(String title, String currentValue, boolean isPhone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update " + title);

        final EditText input = new EditText(this);
        input.setText(currentValue);
        input.setPadding(50, 40, 50, 40);
        if (isPhone) input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        else input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (newValue.isEmpty()) {
                Toast.makeText(this, "Value cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (isPhone) userPhone = newValue;
            else userPassword = newValue;

            db.updateUserProfile(userEmail, userPhone, userPassword);
            Toast.makeText(this, title + " updated successfully", Toast.LENGTH_SHORT).show();
            loadUserProfile();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
