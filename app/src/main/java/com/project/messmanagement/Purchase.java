package com.project.messmanagement;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases")
public class Purchase {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String date;
    public String price;
    public int userId;

    public Purchase(String name, String date, String price, int userId) {
        this.name = name;
        this.date = date;
        this.price = price;
        this.userId = userId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getPrice() { return price; }
    public int getUserId() { return userId; }
}
