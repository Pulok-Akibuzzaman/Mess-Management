package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.project.messmanagement.repositories.AuthRepository;

/**
 * Login Activity - User authentication
 * Uses SharedPreferences for data persistence
 * Navigates to MainActivity on successful login
 */
public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button btnSignIn;
    private TextView tvSignUpLink;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);

        // Check if already logged in
        if (authRepository.isLoggedIn()) {
            navigateToDashboard();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnSignIn = findViewById(R.id.btn_signin);
        tvSignUpLink = findViewById(R.id.tv_signup_link);
    }

    private void setupListeners() {
        btnSignIn.setOnClickListener(v -> performLogin());
        tvSignUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            return;
        }

        if (password.length() < 4) {
            passwordInput.setError("Password must be at least 4 characters");
            return;
        }

        // Perform login
        boolean success = authRepository.login(email, password);

        if (success) {
            Toast.makeText(this, "Login successful! Welcome " + authRepository.getCurrentUserName(),
                    Toast.LENGTH_SHORT).show();
            navigateToDashboard();
        } else {
            Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}