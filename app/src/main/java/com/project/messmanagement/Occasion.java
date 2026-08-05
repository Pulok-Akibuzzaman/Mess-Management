package com.project.messmanagement;

public class Occasion {
    public int id;
    public String title;
    public String type; // Festival, Social, Birthday, etc.
    public double totalCost;
    public int memberCount;
    public String date;

    public Occasion(int id, String title, String type, double totalCost, int memberCount, String date) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.totalCost = totalCost;
        this.memberCount = memberCount;
        this.date = date;
    }
}
