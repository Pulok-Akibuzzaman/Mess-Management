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
import org.json.JSONArray;
import org.json.JSONObject;

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
                    // Final Fallback: Check Supabase (Cloud Login)
                    checkSupabaseLogin(email, password);
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

    private void checkSupabaseLogin(final String email, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Encode the email for the URL
                String encodedEmail = email;
                try {
                    encodedEmail = java.net.URLEncoder.encode(email, "UTF-8");
                } catch (Exception ignored) {}

                // Supabase query: Select user where email matches
                String response = RemoteAccess.getInstance().makeSupabaseRequest("members?email=eq." + encodedEmail, "GET", null);
                System.out.println("@SupabaseResponse: " + response); // Debug: See exactly what Supabase says
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response != null && !response.isEmpty()) {
                                if (response.trim().startsWith("{")) {
                                    // It's an error object
                                    JSONObject errorObj = new JSONObject(response);
                                    String msg = errorObj.optString("message", "Unknown cloud error");
                                    Toast.makeText(LoginActivity.this, "Cloud Error: " + msg, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                JSONArray array = new JSONArray(response);
                                if (array.length() > 0) {
                                    JSONObject user = array.getJSONObject(0);
                                    String cloudPassword = user.getString("password");
                                    
                                    if (cloudPassword.equals(password)) {
                                        // Match! Save to local SQLite so it works offline next time
                                        String name = user.getString("name");
                                        String status = user.getString("status");
                                        String phone = user.optString("phone", "");
                                        String room = user.optString("room", "N/A");
                                        double paid = user.optDouble("paid_amount", 0.0);

                                        dbHelper.addMember(name, room, status, email, phone, "Cloud Sync", password, paid);
                                        
                                        saveSessionAndContinue(email, name, status);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "User not found in Cloud", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed: Check internet connection", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Response Parse Error: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
