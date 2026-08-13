package com.project.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class LoginActivity extends AppCompatActivity {

    // 1. Declare variables for UI elements
    EditText emailInput, passwordInput;
    Button btnSignIn;
    TextView tvSignup;
    CheckBox cbRememberMe, cbRememberLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnSignIn = findViewById(R.id.btn_signin);
        tvSignup = findViewById(R.id.tv_signup_link);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        cbRememberLogin = findViewById(R.id.cb_remember_login);

        // Check for saved preferences
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        
        // 1. Check "Remember Me" (Pre-fill Email)
        boolean rememberMe = pref.getBoolean("rememberMe", false);
        if (rememberMe) {
            String savedEmail = pref.getString("savedEmail", "");
            emailInput.setText(savedEmail);
            cbRememberMe.setChecked(true);
        }

        // 2. Check "Remember Login" (Auto-Login)
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

        // Set click listener for Sign In button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim().toLowerCase();
                String password = passwordInput.getText().toString().trim();

                // Logic: Check SQLite Database FIRST
                Cursor userCursor = dbHelper.checkLogin(email, password);
                
                if (userCursor != null && userCursor.moveToFirst()) {
                    String nameToPass = userCursor.getString(userCursor.getColumnIndexOrThrow("name"));
                    String roleToPass = userCursor.getString(userCursor.getColumnIndexOrThrow("status"));

                    saveSessionAndContinue(email, nameToPass, roleToPass);
                    userCursor.close();
                } 
                // Fallback: Check hardcoded admin
                else if (email.equals("admin@mess.com")) {
                    Cursor adminCheck = dbHelper.checkLogin(email, password);
                    if (adminCheck != null && adminCheck.moveToFirst()) {
                        String nameToPass = adminCheck.getString(adminCheck.getColumnIndexOrThrow("name"));
                        saveSessionAndContinue(email, nameToPass, "Admin");
                        adminCheck.close();
                    } else if (password.equals("1234")) {
                        saveSessionAndContinue(email, "Mess Admin", "Admin");
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for Sign Up link
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveSessionAndContinue(String email, String name, String role) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // Handle "Remember Me"
        if (cbRememberMe.isChecked()) {
            editor.putBoolean("rememberMe", true);
            editor.putString("savedEmail", email);
        } else {
            editor.putBoolean("rememberMe", false);
            editor.remove("savedEmail");
        }

        // Handle "Remember Login"
        if (cbRememberLogin.isChecked()) {
            editor.putBoolean("isLoggedIn", true);
        } else {
            editor.putBoolean("isLoggedIn", false);
        }

        // Save generic user info
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("role", role);
        editor.apply();

        Toast.makeText(LoginActivity.this, "Login Successful as " + role, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_NAME", name);
        intent.putExtra("USER_ROLE", role);
        startActivity(intent);
        finish();
    }
}
