package com.project.messmanagement;

public class CashTransaction {
    int id;
    String description, type, date, performedBy, memberEmail;
    Double amount;

    CashTransaction(int id, String description, Double amount, String type, String date, String performedBy, String memberEmail){
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.performedBy = performedBy;
        this.memberEmail = memberEmail;
    }
}
