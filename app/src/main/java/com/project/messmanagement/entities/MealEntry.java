package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * MealEntry entity for tracking individual meal attendance
 */
@Entity(tableName = "meal_entries")
public class MealEntry {
    @PrimaryKey(autoGenerate = true)
    public int entryId;

    public int mealId; // Foreign key to Meal
    public int userId;
    public String status; // Present, Absent, Pending
    public String createdAt;

    public MealEntry() {}

    public MealEntry(int mealId, int userId, String status, String createdAt) {
        this.mealId = mealId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MealEntry{" +
                "entryId=" + entryId +
                ", mealId=" + mealId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                '}';
    }
}
