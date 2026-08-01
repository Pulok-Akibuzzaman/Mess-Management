package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * MonthlyReport entity for tracking monthly financial summary
 */
@Entity(tableName = "monthly_reports")
public class MonthlyReport {
    @PrimaryKey(autoGenerate = true)
    public int reportId;

    public String month; // Format: YYYY-MM
    public double totalIncome;
    public double totalExpense;
    public int totalMeals;
    public double averagePerPerson;
    public double mealRatePerPerson;
    public String generatedAt;

    public MonthlyReport() {}

    public MonthlyReport(String month, double totalIncome, double totalExpense,
                       int totalMeals, double averagePerPerson, double mealRatePerPerson,
                       String generatedAt) {
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalMeals = totalMeals;
        this.averagePerPerson = averagePerPerson;
        this.mealRatePerPerson = mealRatePerPerson;
        this.generatedAt = generatedAt;
    }

    @Override
    public String toString() {
        return "MonthlyReport{" +
                "reportId=" + reportId +
                ", month='" + month + '\'' +
                ", totalIncome=" + totalIncome +
                ", totalExpense=" + totalExpense +
                '}';
    }
}
