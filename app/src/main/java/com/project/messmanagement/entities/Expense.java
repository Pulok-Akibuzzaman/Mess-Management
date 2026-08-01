package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Expense entity for tracking expenses by category
 */
@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int expenseId;

    public String category; // Bazar, Utility, Bua Salary, Gas Bill, Other
    public double amount;
    public String date; // Format: YYYY-MM-DD
    public String description;
    public String createdBy; // Email from SharedPreferences
    public String createdAt;

    public Expense() {}

    public Expense(String category, double amount, String date,
                  String description, String createdBy, String createdAt) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                '}';
    }
}
