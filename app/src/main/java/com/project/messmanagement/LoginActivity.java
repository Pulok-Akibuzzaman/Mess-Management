package com.project.messmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button btnAdmin, btnMember, btnBus, btnSignIn;
    private String selectedRole = "Admin"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnAdmin = findViewById(R.id.btn_admin);
        btnMember = findViewById(R.id.btn_member);
        btnBus = findViewById(R.id.btn_bus);
        btnSignIn = findViewById(R.id.btn_signin);

        // Role Selection Listeners
        btnAdmin.setOnClickListener(v -> selectRole("Admin"));
        btnMember.setOnClickListener(v -> selectRole("Member"));
        btnBus.setOnClickListener(v -> selectRole("Bus"));

        // Sign In Listener
        btnSignIn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Logging in as " + selectedRole, Toast.LENGTH_SHORT).show();
                // TODO: Implement actual login logic
            }
        });
    }

    private void selectRole(String role) {
        selectedRole = role;

        // Reset all buttons to inactive
        resetButtonStyle(btnAdmin);
        resetButtonStyle(btnMember);
        resetButtonStyle(btnBus);

        // Set selected button to active
        if (role.equals("Admin")) {
            setActiveButtonStyle(btnAdmin);
        } else if (role.equals("Member")) {
            setActiveButtonStyle(btnMember);
        } else if (role.equals("Bus")) {
            setActiveButtonStyle(btnBus);
        }
    }

    private void setActiveButtonStyle(Button button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_admin_active));
        button.setTextColor(ContextCompat.getColor(this, R.color.text_white));
    }

    private void resetButtonStyle(Button button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_inactive));
        button.setTextColor(ContextCompat.getColor(this, R.color.text_light_blue));
    }
}