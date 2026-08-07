package com.project.messmanagement;

public class Equipment {
    int id;
    String name, location, status, date;
    double price;

    public Equipment(int id, String name, String location, String status, String date, double price) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = status;
        this.date = date;
        this.price = price;
    }
}
