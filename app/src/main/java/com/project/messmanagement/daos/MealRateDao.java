package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.MealRate;
import java.util.List;

/**
 * DAO for MealRate entity
 */
@Dao
public interface MealRateDao {
    @Insert
    long insertMealRate(MealRate rate);

    @Update
    int updateMealRate(MealRate rate);

    @Query("SELECT * FROM meal_rates WHERE month = :month LIMIT 1")
    MealRate getMealRateByMonth(String month);

    @Query("SELECT * FROM meal_rates ORDER BY month DESC LIMIT 1")
    MealRate getLatestMealRate();

    @Query("SELECT * FROM meal_rates ORDER BY month DESC")
    LiveData<List<MealRate>> getAllMealRates();

    @Query("SELECT * FROM meal_rates WHERE ratePerPerson > 0 LIMIT 1")
    MealRate getAnyMealRate();
}
