package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etPassword;
    private MaterialButton btnContinue;
    private TextView tvSignInLink;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        try {
            database = AppDatabase.getDatabase(this);

            etFullName = findViewById(R.id.et_full_name);
            etEmail = findViewById(R.id.et_email);
            etPhone = findViewById(R.id.et_phone);
            etPassword = findViewById(R.id.et_password);
            btnContinue = findViewById(R.id.btn_continue_container);
            tvSignInLink = findViewById(R.id.tv_signin_link);

            if (btnContinue != null) {
                btnContinue.setOnClickListener(v -> performSignup());
            }

            if (tvSignInLink != null) {
                tvSignInLink.setOnClickListener(v -> {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing signup: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performSignup() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User existingUser = database.userDao().getUserByEmail(email);
            runOnUiThread(() -> {
                if (existingUser != null) {
                    Toast.makeText(SignupActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                } else {
                    User newUser = new User(name, email, phone, password, "Member");
                    new Thread(() -> {
                        database.userDao().insertUser(newUser);
                        runOnUiThread(() -> {
                            Toast.makeText(SignupActivity.this, "Account created for " + name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        });
                    }).start();
                }
            });
        }).start();
    }
}