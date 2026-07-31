package com.project.messmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button btnAdmin, btnMember, btnBus, btnSignIn;
    private TextView tvSignUpLink;
    private String selectedRole = "Admin";
    private SessionManager sessionManager;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        database = AppDatabase.getDatabase(this);

        initializeViews();

        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        setupListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnAdmin = findViewById(R.id.btn_admin);
        btnMember = findViewById(R.id.btn_member);
        btnBus = findViewById(R.id.btn_bus);
        btnSignIn = findViewById(R.id.btn_signin);
        tvSignUpLink = findViewById(R.id.tv_signup_link);

        selectRole("Admin");
    }

    private void setupListeners() {
        btnAdmin.setOnClickListener(v -> selectRole("Admin"));
        btnMember.setOnClickListener(v -> selectRole("Member"));
        btnBus.setOnClickListener(v -> selectRole("Bus"));

        tvSignUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User user = database.userDao().loginUser(email, password);
            runOnUiThread(() -> {
                if (user != null) {
                    sessionManager.createLoginSession(user.id, user.email, user.name, user.role);
                    Toast.makeText(LoginActivity.this, "Login successful! Welcome " + user.name, Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void selectRole(String role) {
        selectedRole = role;

        resetButtonStyle(btnAdmin);
        resetButtonStyle(btnMember);
        resetButtonStyle(btnBus);

        if (role.equals("Admin")) {
            setActiveButtonStyle(btnAdmin);
        } else if (role.equals("Member")) {
            setActiveButtonStyle(btnMember);
        } else if (role.equals("Bus")) {
            setActiveButtonStyle(btnBus);
        }
    }

    private void setActiveButtonStyle(Button button) {
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.nav_active));
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void resetButtonStyle(Button button) {
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.input_bg));
        button.setTextColor(ContextCompat.getColor(this, R.color.text_light_blue));
    }
}