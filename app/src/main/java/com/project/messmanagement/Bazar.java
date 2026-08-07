package com.project.messmanagement;

public class Bazar {
    int id;
    String name, date, boughtBy;
    Double amount;

    Bazar(int id, String name, String date, Double amount, String boughtBy){
        this.id = id;
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.boughtBy = boughtBy;
    }
}
