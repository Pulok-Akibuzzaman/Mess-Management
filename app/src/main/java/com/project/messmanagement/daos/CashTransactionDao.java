package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.CashTransaction;
import java.util.List;

/**
 * DAO for CashTransaction entity
 */
@Dao
public interface CashTransactionDao {
    @Insert
    long insertTransaction(CashTransaction transaction);

    @Update
    int updateTransaction(CashTransaction transaction);

    @Delete
    int deleteTransaction(CashTransaction transaction);

    @Query("SELECT * FROM cash_transactions WHERE transactionId = :transactionId")
    CashTransaction getTransactionById(int transactionId);

    @Query("SELECT * FROM cash_transactions ORDER BY date DESC")
    LiveData<List<CashTransaction>> getAllTransactions();

    @Query("SELECT * FROM cash_transactions WHERE type = :type ORDER BY date DESC")
    LiveData<List<CashTransaction>> getTransactionsByType(String type);

    @Query("SELECT * FROM cash_transactions WHERE date LIKE :month || '%' ORDER BY date DESC")
    LiveData<List<CashTransaction>> getTransactionsByMonth(String month);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM cash_transactions WHERE type = 'Income' AND date LIKE :month || '%'")
    double getTotalIncomeByMonth(String month);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM cash_transactions WHERE type = 'Expense' AND date LIKE :month || '%'")
    double getTotalExpenseByMonth(String month);

    @Query("SELECT (SELECT COALESCE(SUM(amount), 0) FROM cash_transactions WHERE type = 'Income' AND date LIKE :month || '%') - (SELECT COALESCE(SUM(amount), 0) FROM cash_transactions WHERE type = 'Expense' AND date LIKE :month || '%')")
    double getMonthlyBalance(String month);

    @Query("SELECT * FROM cash_transactions ORDER BY date DESC LIMIT :limit")
    LiveData<List<CashTransaction>> getRecentTransactions(int limit);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM cash_transactions WHERE type = 'Income'")
    double getTotalIncome();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM cash_transactions WHERE type = 'Expense'")
    double getTotalExpense();
}
