package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.MealEntry;
import java.util.List;

/**
 * DAO for MealEntry entity
 */
@Dao
public interface MealEntryDao {
    @Insert
    long insertMealEntry(MealEntry entry);

    @Update
    int updateMealEntry(MealEntry entry);

    @Delete
    int deleteMealEntry(MealEntry entry);

    @Query("SELECT * FROM meal_entries WHERE mealId = :mealId")
    List<MealEntry> getMealEntriesByMealId(int mealId);

    @Query("SELECT * FROM meal_entries WHERE userId = :userId")
    List<MealEntry> getMealEntriesByUserId(int userId);

    @Query("SELECT * FROM meal_entries WHERE userId = :userId AND createdAt LIKE :month || '%'")
    LiveData<List<MealEntry>> getMealEntriesByUserAndMonth(int userId, String month);

    @Query("SELECT COUNT(*) FROM meal_entries WHERE userId = :userId AND createdAt LIKE :month || '%' AND status = 'Present'")
    int getMealCountByUserAndMonth(int userId, String month);

    @Query("SELECT COUNT(*) FROM meals WHERE mealType = :mealType AND date LIKE :month || '%'")
    int getMealCountByTypeAndMonth(String mealType, String month);

    @Query("SELECT COUNT(*) FROM meal_entries WHERE status = 'Present'")
    int getTotalPresentMeals();
}
