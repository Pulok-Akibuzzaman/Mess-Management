package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    // Declare UI elements
    EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    Button btnSignup, btnRoleMember, btnRoleBua;
    TextView tvLogin;
    String selectedRole = "Member"; // Default role for signup
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = new DatabaseHelper(this);

        // Initialize UI elements
        etName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_continue_container);
        tvLogin = findViewById(R.id.tv_signin_link);
        btnRoleMember = findViewById(R.id.btn_signup_member);
        btnRoleBua = findViewById(R.id.btn_signup_bua);

        // Role Button Clicks
        btnRoleMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSignupRole("Member");
            }
        });

        btnRoleBua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSignupRole("Bua");
            }
        });

        // Signup Button Click
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim().toLowerCase();
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // 1. Save to SQLite Database
                    long result = db.addMember(name, "N/A", selectedRole, email, phone, "Just Now", password, 0.0);

                    if (result == -1) {
                        Toast.makeText(SignupActivity.this, "Signup Failed: Email might be taken or database error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 2. Save session to SharedPreferences
                    SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("email", email);
                    editor.putString("name", name);
                    editor.putString("role", selectedRole);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(SignupActivity.this, "Signup Successful as " + selectedRole, Toast.LENGTH_SHORT).show();

                    // 2. Navigate to MainActivity and pass the Name and Role
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra("USER_NAME", name);
                    intent.putExtra("USER_ROLE", selectedRole);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Login Link Click
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateSignupRole(String role) {
        selectedRole = role;

        // Reset colors
        btnRoleMember.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.input_bg)));
        btnRoleBua.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.input_bg)));

        btnRoleMember.setTextColor(getResources().getColor(R.color.text_light_blue));
        btnRoleBua.setTextColor(getResources().getColor(R.color.text_light_blue));

        // Highlight selected
        if (role.equals("Member")) {
            btnRoleMember.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_active)));
            btnRoleMember.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnRoleBua.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_active)));
            btnRoleBua.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
