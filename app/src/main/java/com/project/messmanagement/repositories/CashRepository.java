package com.project.messmanagement.repositories;

import androidx.lifecycle.LiveData;
import com.project.messmanagement.MessMateDatabase;
import com.project.messmanagement.entities.CashTransaction;
import com.project.messmanagement.utils.DateUtils;
import java.util.List;

/**
 * Repository for Cash/Transaction operations
 */
public class CashRepository {
    private MessMateDatabase database;
    private String currentUserEmail;

    public CashRepository(MessMateDatabase database, String userEmail) {
        this.database = database;
        this.currentUserEmail = userEmail;
    }

    /**
     * Add new transaction
     */
    public void addTransaction(String type, String category, double amount,
                              String description, String date) {
        new Thread(() -> {
            CashTransaction transaction = new CashTransaction(
                type,
                category,
                amount,
                description,
                date,
                currentUserEmail,
                DateUtils.getCurrentDateTime()
            );
            database.cashTransactionDao().insertTransaction(transaction);
        }).start();
    }

    /**
     * Get all transactions
     */
    public LiveData<List<CashTransaction>> getAllTransactions() {
        return database.cashTransactionDao().getAllTransactions();
    }

    /**
     * Get transactions by month
     */
    public LiveData<List<CashTransaction>> getTransactionsByMonth(String month) {
        return database.cashTransactionDao().getTransactionsByMonth(month);
    }

    /**
     * Get total income by month
     */
    public double getTotalIncomeByMonth(String month) {
        try {
            return database.cashTransactionDao().getTotalIncomeByMonth(month);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get total expense by month
     */
    public double getTotalExpenseByMonth(String month) {
        try {
            return database.cashTransactionDao().getTotalExpenseByMonth(month);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get monthly balance
     */
    public double getMonthlyBalance(String month) {
        try {
            return database.cashTransactionDao().getMonthlyBalance(month);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get current month balance
     */
    public double getCurrentMonthBalance() {
        try {
            String currentMonth = DateUtils.getCurrentMonth();
            return getMonthlyBalance(currentMonth);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get recent transactions
     */
    public LiveData<List<CashTransaction>> getRecentTransactions(int limit) {
        return database.cashTransactionDao().getRecentTransactions(limit);
    }

    /**
     * Delete transaction
     */
    public void deleteTransaction(int transactionId) {
        new Thread(() -> {
            CashTransaction transaction = database.cashTransactionDao().getTransactionById(transactionId);
            if (transaction != null) {
                database.cashTransactionDao().deleteTransaction(transaction);
            }
        }).start();
    }

    /**
     * Get total income (all time)
     */
    public double getTotalIncome() {
        try {
            return database.cashTransactionDao().getTotalIncome();
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get total expense (all time)
     */
    public double getTotalExpense() {
        try {
            return database.cashTransactionDao().getTotalExpense();
        } catch (Exception e) {
            return 0.0;
        }
    }
}
