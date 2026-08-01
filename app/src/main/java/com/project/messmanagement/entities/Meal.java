package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Meal entity for tracking daily meals
 */
@Entity(tableName = "meals")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    public int mealId;

    public String mealType; // Breakfast, Lunch, Dinner
    public String date; // Format: YYYY-MM-DD
    public String createdBy; // Email from SharedPreferences
    public String createdAt; // LocalDateTime string

    public Meal() {}

    public Meal(String mealType, String date, String createdBy, String createdAt) {
        this.mealType = mealType;
        this.date = date;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "mealId=" + mealId +
                ", mealType='" + mealType + '\'' +
                ", date='" + date + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
