package com.project.messmanagement;

public class Complaint {
    int id;
    String message, date, addedBy;

    public Complaint(int id, String message, String date, String addedBy) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.addedBy = addedBy;
    }
}
