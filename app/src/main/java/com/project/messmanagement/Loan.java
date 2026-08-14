package com.project.messmanagement;

public class Loan {
    int id;
    String lender, status, date, addedBy;
    double amount;

    public Loan(int id, String lender, double amount, String status, String date, String addedBy) {
        this.id = id;
        this.lender = lender;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.addedBy = addedBy;
    }
}
