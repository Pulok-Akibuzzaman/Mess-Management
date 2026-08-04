package com.project.messmanagement;

public class CashTransaction {
    int id;
    String description, type, date;
    Double amount;

    CashTransaction(int id, String description, Double amount, String type, String date){
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }
}
