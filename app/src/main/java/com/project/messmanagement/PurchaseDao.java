package com.project.messmanagement;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PurchaseDao {
    @Insert
    void insertPurchase(Purchase purchase);

    @Update
    void updatePurchase(Purchase purchase);

    @Delete
    void deletePurchase(Purchase purchase);

    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY id DESC")
    List<Purchase> getPurchasesByUser(int userId);

    @Query("SELECT * FROM purchases ORDER BY id DESC")
    List<Purchase> getAllPurchases();

    @Query("SELECT * FROM purchases WHERE id = :id LIMIT 1")
    Purchase getPurchaseById(int id);
}
