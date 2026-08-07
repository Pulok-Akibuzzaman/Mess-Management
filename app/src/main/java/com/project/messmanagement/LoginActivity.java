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
import android.database.Cursor;

public class LoginActivity extends AppCompatActivity {

    // 1. Declare variables for UI elements
    EditText emailInput, passwordInput;
    Button btnSignIn;
    TextView tvSignup;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

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


        // 4. Set click listener for Sign In button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim().toLowerCase();
                String password = passwordInput.getText().toString().trim();

                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);

                // 2. Logic: Check SQLite Database FIRST
                Cursor userCursor = dbHelper.checkLogin(email, password);
                
                if (userCursor != null && userCursor.moveToFirst()) {
                    String nameToPass = userCursor.getString(userCursor.getColumnIndexOrThrow("name"));
                    String roleToPass = userCursor.getString(userCursor.getColumnIndexOrThrow("status")); // Role is stored in status for simplicity

                    // Save Session
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("email", email);
                    editor.putString("name", nameToPass);
                    editor.putString("role", roleToPass);
                    editor.apply();

                    userCursor.close();
                    Toast.makeText(LoginActivity.this, "Login Successful as " + roleToPass, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_NAME", nameToPass);
                    intent.putExtra("USER_ROLE", roleToPass);
                    startActivity(intent);
                    finish();
                } 
                // 3. Fallback: Check hardcoded admin (Double check DB for updated password first)
                else if (email.equals("admin@mess.com")) {
                    // Check if Admin exists in DB (meaning they might have updated their password)
                    Cursor adminCheck = dbHelper.checkLogin(email, password);
                    if (adminCheck != null && adminCheck.moveToFirst()) {
                        String nameToPass = adminCheck.getString(adminCheck.getColumnIndexOrThrow("name"));
                        
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("email", email);
                        editor.putString("name", nameToPass);
                        editor.putString("role", "Admin");
                        editor.apply();
                        adminCheck.close();

                        Toast.makeText(LoginActivity.this, "Login Successful as Admin", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USER_NAME", nameToPass);
                        intent.putExtra("USER_ROLE", "Admin");
                        startActivity(intent);
                        finish();
                    } else if (password.equals("1234")) {
                        // Original hardcoded fallback
                        String nameToPass = "Mess Admin";
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("email", email);
                        editor.putString("name", nameToPass);
                        editor.putString("role", "Admin");
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Login Successful as Admin", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USER_NAME", nameToPass);
                        intent.putExtra("USER_ROLE", "Admin");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
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

}
