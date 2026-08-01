package com.project.messmanagement.repositories;

import android.content.Context;
import com.project.messmanagement.SharedPreferencesManager;

/**
 * Repository for authentication operations
 */
public class AuthRepository {
    private SharedPreferencesManager sharedPreferencesManager;

    public AuthRepository(Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    /**
     * Login user with email and password
     * For demo: checks hardcoded credentials or any email/password (auto-approve)
     */
    public boolean login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        // Auto-approve login for demo
        String fullName = email.split("@")[0];
        sharedPreferencesManager.saveLoginData(
            1, // userId
            fullName, // fullName
            email, // email
            "123456", // phone
            password, // password
            "Admin", // role
            "" // profilePicture
        );
        return true;
    }

    /**
     * Sign up new user
     */
    public boolean signup(String fullName, String email, String phone,
                         String password, String role) {
        if (fullName == null || fullName.isEmpty() ||
            email == null || email.isEmpty() ||
            phone == null || phone.isEmpty() ||
            password == null || password.isEmpty()) {
            return false;
        }

        sharedPreferencesManager.saveLoginData(
            (int) (System.currentTimeMillis() / 1000), // userId (timestamp-based)
            fullName, // fullName
            email, // email
            phone, // phone
            password, // password
            role != null ? role : "Member", // role
            "" // profilePicture
        );
        return true;
    }

    /**
     * Logout user
     */
    public void logout() {
        sharedPreferencesManager.clearLoginData();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferencesManager.isUserLoggedIn();
    }

    /**
     * Get current user
     */
    public SharedPreferencesManager.User getCurrentUser() {
        return sharedPreferencesManager.getLoginData();
    }

    /**
     * Get current user email
     */
    public String getCurrentUserEmail() {
        return sharedPreferencesManager.getUserEmail();
    }

    /**
     * Get current user name
     */
    public String getCurrentUserName() {
        return sharedPreferencesManager.getUserFullName();
    }

    /**
     * Get current user role
     */
    public String getCurrentUserRole() {
        return sharedPreferencesManager.getUserRole();
    }
}
