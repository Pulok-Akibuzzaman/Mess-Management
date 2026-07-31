package com.project.messmanagement;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insertTransaction(Transaction transaction);

    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY id DESC")
    List<Transaction> getTransactionsByUser(int userId);

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    Transaction getTransactionById(int id);
}
