package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.Meal;
import java.util.List;

/**
 * DAO for Meal entity
 */
@Dao
public interface MealDao {
    @Insert
    long insertMeal(Meal meal);

    @Update
    int updateMeal(Meal meal);

    @Delete
    int deleteMeal(Meal meal);

    @Query("SELECT * FROM meals WHERE mealId = :mealId")
    Meal getMealById(int mealId);

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY mealId DESC")
    List<Meal> getMealsByDate(String date);

    @Query("SELECT * FROM meals WHERE date LIKE :month || '%' ORDER BY date DESC")
    LiveData<List<Meal>> getMealsByMonth(String month);

    @Query("SELECT * FROM meals ORDER BY date DESC")
    LiveData<List<Meal>> getAllMeals();

    @Query("SELECT * FROM meals WHERE date = DATE('now') ORDER BY mealId")
    LiveData<List<Meal>> getTodaysMeals();

    @Query("SELECT COUNT(*) FROM meals WHERE date LIKE :month || '%'")
    int getMealCountByMonth(String month);

    @Query("SELECT COUNT(*) FROM meals")
    int getTotalMeals();
}
