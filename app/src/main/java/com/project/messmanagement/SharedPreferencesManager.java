package com.project.messmanagement;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user login/authentication data using SharedPreferences
 * Stores: userId, fullName, email, phone, role, profilePicture
 */
public class SharedPreferencesManager {
    private static final String PREF_NAME = "MessMateAppPreferences";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PROFILE_PICTURE = "profilePicture";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save login data after successful authentication
     */
    public void saveLoginData(int userId, String fullName, String email, String phone,
                             String password, String role, String profilePicture) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_PROFILE_PICTURE, profilePicture != null ? profilePicture : "");
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Get current logged-in user data
     */
    public User getLoginData() {
        if (!isUserLoggedIn()) {
            return null;
        }

        User user = new User();
        user.id = getUserId();
        user.fullName = getUserFullName();
        user.email = getUserEmail();
        user.phone = getUserPhone();
        user.password = getPassword();
        user.role = getUserRole();
        user.profilePicture = getProfilePicture();
        return user;
    }

    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user role (Admin, Member, Bua, etc.)
     */
    public String getUserRole() {
        return sharedPreferences.getString(KEY_ROLE, "");
    }

    /**
     * Get user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get user full name
     */
    public String getUserFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "Guest");
    }

    /**
     * Get user phone
     */
    public String getUserPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }

    /**
     * Get user password
     */
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    /**
     * Get user profile picture URL
     */
    public String getProfilePicture() {
        return sharedPreferences.getString(KEY_PROFILE_PICTURE, "");
    }

    /**
     * Update profile picture URL
     */
    public void updateProfilePicture(String url) {
        editor.putString(KEY_PROFILE_PICTURE, url);
        editor.apply();
    }

    /**
     * Clear all login data (on logout)
     */
    public void clearLoginData() {
        editor.clear();
        editor.apply();
    }

    /**
     * Simple User class for storing current user data
     */
    public static class User {
        public int id;
        public String fullName;
        public String email;
        public String phone;
        public String password;
        public String role;
        public String profilePicture;

        public User() {}

        public User(int id, String fullName, String email, String phone,
                   String password, String role, String profilePicture) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.role = role;
            this.profilePicture = profilePicture;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", fullName='" + fullName + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}
