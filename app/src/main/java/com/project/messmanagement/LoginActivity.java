package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // 1. Declare variables for UI elements
    EditText emailInput, passwordInput;
    Button btnSignIn, btnAdmin, btnMember, btnBua;
    TextView tvSignup;
    String selectedRole = "Admin"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Check if user is already logged in (Auto-Login)
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String savedName = pref.getString("name", "User");
            String savedRole = pref.getString("role", "Admin");
            
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_NAME", savedName);
            intent.putExtra("USER_ROLE", savedRole);
            startActivity(intent);
            finish();
            return;
        }

        // 2. Initialize UI elements using findViewById
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnSignIn = findViewById(R.id.btn_signin);
        tvSignup = findViewById(R.id.tv_signup_link);
        btnAdmin = findViewById(R.id.btn_admin);
        btnMember = findViewById(R.id.btn_member);
        btnBua = findViewById(R.id.btn_bus);

        // 3. Set click listeners for Role Buttons
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRoleSelection("Admin");
            }
        });

        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRoleSelection("Member");
            }
        });

        btnBua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRoleSelection("Bua");
            }
        });

        // 4. Set click listener for Sign In button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                // 1. Get saved data from SharedPreferences
                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String savedEmail = pref.getString("email", "");
                String savedPassword = pref.getString("password", "");
                String savedName = pref.getString("name", "User");
                String savedRole = pref.getString("role", "Admin");

                // 2. Logic: Check against saved data OR hardcoded admin
                if ((email.equals(savedEmail) && password.equals(savedPassword)) || 
                    (email.equals("admin@mess.com") && password.equals("1234"))) {
                    
                    String nameToPass = email.equals(savedEmail) ? savedName : "Admin User";
                    String roleToPass = email.equals(savedEmail) ? savedRole : selectedRole;

                    // Save Login State
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login Successful as " + roleToPass, Toast.LENGTH_SHORT).show();

                    // Navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_NAME", nameToPass);
                    intent.putExtra("USER_ROLE", roleToPass);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Set click listener for Sign Up link
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity using Explicit Intent
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // Beginner Method to update the visual state of role buttons
    private void updateRoleSelection(String role) {
        selectedRole = role;

        // Reset all buttons to inactive state
        btnAdmin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.input_bg)));
        btnMember.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.input_bg)));
        btnBua.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.input_bg)));

        btnAdmin.setTextColor(getResources().getColor(R.color.text_light_blue));
        btnMember.setTextColor(getResources().getColor(R.color.text_light_blue));
        btnBua.setTextColor(getResources().getColor(R.color.text_light_blue));

        // Highlight the selected button
        if (role.equals("Admin")) {
            btnAdmin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_active)));
            btnAdmin.setTextColor(getResources().getColor(R.color.white));
        } else if (role.equals("Member")) {
            btnMember.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_active)));
            btnMember.setTextColor(getResources().getColor(R.color.white));
        } else if (role.equals("Bua")) {
            btnBua.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_active)));
            btnBua.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
