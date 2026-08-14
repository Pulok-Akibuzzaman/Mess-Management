package com.project.messmanagement;

public class Occasion {
    int id;
    String title, type, date, addedBy;
    double totalCost;
    int memberCount;

    public Occasion(int id, String title, String type, double totalCost, int memberCount, String date, String addedBy) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.totalCost = totalCost;
        this.memberCount = memberCount;
        this.date = date;
        this.addedBy = addedBy;
    }
}
