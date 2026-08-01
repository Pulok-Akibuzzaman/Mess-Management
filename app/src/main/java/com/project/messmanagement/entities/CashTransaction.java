package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * CashTransaction entity for tracking cash in/out
 */
@Entity(tableName = "cash_transactions")
public class CashTransaction {
    @PrimaryKey(autoGenerate = true)
    public int transactionId;

    public String type; // Income, Expense
    public String category; // Monthly Collection, Bazar, Salary, Bill, Other
    public double amount;
    public String description;
    public String date; // Format: YYYY-MM-DD
    public String createdBy; // Email from SharedPreferences
    public String createdAt;

    public CashTransaction() {}

    public CashTransaction(String type, String category, double amount,
                          String description, String date, String createdBy, String createdAt) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CashTransaction{" +
                "transactionId=" + transactionId +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                '}';
    }
}
