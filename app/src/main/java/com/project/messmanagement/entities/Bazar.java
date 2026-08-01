package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Bazar entity for tracking shopping/purchases
 */
@Entity(tableName = "bazars")
public class Bazar {
    @PrimaryKey(autoGenerate = true)
    public int bazarId;

    public String itemName;
    public double amount;
    public String date; // Format: YYYY-MM-DD
    public String category; // Vegetables, Rice, Meat, Oil, Spices, Dairy, Other
    public String createdBy; // Email from SharedPreferences
    public String description;
    public String createdAt;

    public Bazar() {}

    public Bazar(String itemName, double amount, String date, String category,
                String createdBy, String description, String createdAt) {
        this.itemName = itemName;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.createdBy = createdBy;
        this.description = description;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Bazar{" +
                "bazarId=" + bazarId +
                ", itemName='" + itemName + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
