package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.Expense;
import java.util.List;

/**
 * DAO for Expense entity
 */
@Dao
public interface ExpenseDao {
    @Insert
    long insertExpense(Expense expense);

    @Update
    int updateExpense(Expense expense);

    @Delete
    int deleteExpense(Expense expense);

    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId")
    Expense getExpenseById(int expenseId);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE date LIKE :month || '%' ORDER BY date DESC")
    LiveData<List<Expense>> getExpenseByMonth(String month);

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    List<Expense> getExpenseByCategory(String category);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date LIKE :month || '%'")
    double getExpenseTotalByMonth(String month);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE category = :category")
    double getExpenseTotalByCategory(String category);

    @Query("SELECT COUNT(*) FROM expenses")
    int getTotalExpenses();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    double getTotalExpenseAmount();
}
