package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.Bazar;
import java.util.List;

/**
 * DAO for Bazar entity
 */
@Dao
public interface BazarDao {
    @Insert
    long insertBazar(Bazar bazar);

    @Update
    int updateBazar(Bazar bazar);

    @Delete
    int deleteBazar(Bazar bazar);

    @Query("SELECT * FROM bazars WHERE bazarId = :bazarId")
    Bazar getBazarById(int bazarId);

    @Query("SELECT * FROM bazars ORDER BY date DESC")
    LiveData<List<Bazar>> getAllBazarItems();

    @Query("SELECT * FROM bazars WHERE date LIKE :month || '%' ORDER BY date DESC")
    LiveData<List<Bazar>> getBazarByMonth(String month);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bazars WHERE date LIKE :month || '%'")
    double getBazarTotalByMonth(String month);

    @Query("SELECT * FROM bazars WHERE category = :category ORDER BY date DESC")
    List<Bazar> getBazarByCategory(String category);

    @Query("SELECT * FROM bazars ORDER BY date DESC LIMIT :limit")
    LiveData<List<Bazar>> getRecentBazarItems(int limit);

    @Query("SELECT COUNT(*) FROM bazars WHERE date LIKE :month || '%'")
    int getBazarCountByMonth(String month);

    @Query("SELECT COUNT(*) FROM bazars")
    int getTotalBazarItems();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bazars")
    double getTotalBazarAmount();
}
