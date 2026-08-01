package com.project.messmanagement.repositories;

import androidx.lifecycle.LiveData;
import com.project.messmanagement.MessMateDatabase;
import com.project.messmanagement.entities.Expense;
import com.project.messmanagement.utils.DateUtils;
import java.util.List;

/**
 * Repository for Expense operations
 */
public class ExpenseRepository {
    private MessMateDatabase database;
    private String currentUserEmail;

    public ExpenseRepository(MessMateDatabase database, String userEmail) {
        this.database = database;
        this.currentUserEmail = userEmail;
    }

    /**
     * Add new expense
     */
    public void addExpense(String category, double amount, String date, String description) {
        new Thread(() -> {
            Expense expense = new Expense(
                category,
                amount,
                date,
                description,
                currentUserEmail,
                DateUtils.getCurrentDateTime()
            );
            database.expenseDao().insertExpense(expense);
        }).start();
    }

    /**
     * Get all expenses
     */
    public LiveData<List<Expense>> getAllExpenses() {
        return database.expenseDao().getAllExpenses();
    }

    /**
     * Get expenses by month
     */
    public LiveData<List<Expense>> getExpenseByMonth(String month) {
        return database.expenseDao().getExpenseByMonth(month);
    }

    /**
     * Get expense by category
     */
    public List<Expense> getExpenseByCategory(String category) {
        try {
            return database.expenseDao().getExpenseByCategory(category);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get expense total by month
     */
    public double getExpenseTotalByMonth(String month) {
        try {
            return database.expenseDao().getExpenseTotalByMonth(month);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get expense total by category
     */
    public double getExpenseTotalByCategory(String category) {
        try {
            return database.expenseDao().getExpenseTotalByCategory(category);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Delete expense
     */
    public void deleteExpense(int expenseId) {
        new Thread(() -> {
            Expense expense = database.expenseDao().getExpenseById(expenseId);
            if (expense != null) {
                database.expenseDao().deleteExpense(expense);
            }
        }).start();
    }

    /**
     * Get current month expense total
     */
    public double getCurrentMonthExpenseTotal() {
        try {
            String currentMonth = DateUtils.getCurrentMonth();
            return getExpenseTotalByMonth(currentMonth);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get total expenses (all time)
     */
    public double getTotalExpenseAmount() {
        try {
            return database.expenseDao().getTotalExpenseAmount();
        } catch (Exception e) {
            return 0.0;
        }
    }
}
