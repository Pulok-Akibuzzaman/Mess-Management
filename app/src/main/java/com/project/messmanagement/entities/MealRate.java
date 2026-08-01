package com.project.messmanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * MealRate entity for tracking meal costs per month
 */
@Entity(tableName = "meal_rates")
public class MealRate {
    @PrimaryKey(autoGenerate = true)
    public int rateId;

    public String month; // Format: YYYY-MM
    public double breakfastCost;
    public double lunchCost;
    public double dinnerCost;
    public double ratePerPerson;
    public String createdAt;

    public MealRate() {}

    public MealRate(String month, double breakfastCost, double lunchCost,
                   double dinnerCost, double ratePerPerson, String createdAt) {
        this.month = month;
        this.breakfastCost = breakfastCost;
        this.lunchCost = lunchCost;
        this.dinnerCost = dinnerCost;
        this.ratePerPerson = ratePerPerson;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MealRate{" +
                "rateId=" + rateId +
                ", month='" + month + '\'' +
                ", ratePerPerson=" + ratePerPerson +
                '}';
    }
}
