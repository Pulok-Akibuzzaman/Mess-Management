package com.project.messmanagement;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String description;
    public String date;
    public String amount;
    public boolean isIncoming;
    public int userId;

    public Transaction(String description, String date, String amount, boolean isIncoming, int userId) {
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.isIncoming = isIncoming;
        this.userId = userId;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public boolean isIncoming() { return isIncoming; }
    public int getUserId() { return userId; }
}
