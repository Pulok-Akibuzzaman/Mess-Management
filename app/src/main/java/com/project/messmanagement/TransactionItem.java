package com.project.messmanagement;

public class TransactionItem {
    private String description;
    private String date;
    private String amount;
    private boolean isIncoming;

    public TransactionItem(String description, String date, String amount, boolean isIncoming) {
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.isIncoming = isIncoming;
    }

    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public boolean isIncoming() { return isIncoming; }
}