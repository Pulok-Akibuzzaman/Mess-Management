package com.project.messmanagement;

public class BuaSalary {
    public int id;
    public String monthYear;
    public double amount;
    public String paidDate;
    public String status;

    public BuaSalary(int id, String monthYear, double amount, String paidDate, String status) {
        this.id = id;
        this.monthYear = monthYear;
        this.amount = amount;
        this.paidDate = paidDate;
        this.status = status;
    }
}
