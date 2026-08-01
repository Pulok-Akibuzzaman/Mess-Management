package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.project.messmanagement.repositories.AuthRepository;

/**
 * Signup Activity - User registration
 * Validates input and saves to SharedPreferences
 * Navigates to MainActivity on successful signup
 */
public class SignupActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPhone, etPassword;
    private Button btnContinue;
    private TextView tvSignInLink;
    private Spinner spinnerRole;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        authRepository = new AuthRepository(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnContinue = findViewById(R.id.btn_continue_container);
        tvSignInLink = findViewById(R.id.tv_signin_link);

        try {
            spinnerRole = findViewById(R.id.spinner_role);
        } catch (Exception e) {
            // Role spinner may not exist in layout
        }
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> performSignup());
        tvSignInLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void performSignup() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Full name is required");
            return;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            etPhone.setError("Please enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 4) {
            etPassword.setError("Password must be at least 4 characters");
            return;
        }

        String role = spinnerRole != null ? spinnerRole.getSelectedItem().toString() : "Member";

        // Perform signup
        boolean success = authRepository.signup(name, email, phone, password, role);

        if (success) {
            Toast.makeText(this, "Account created successfully! Welcome " + name, Toast.LENGTH_SHORT).show();
            navigateToDashboard();
        } else {
            Toast.makeText(this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}