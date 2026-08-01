package com.project.messmanagement.repositories;

import androidx.lifecycle.LiveData;
import com.project.messmanagement.MessMateDatabase;
import com.project.messmanagement.entities.Meal;
import com.project.messmanagement.entities.MealEntry;
import com.project.messmanagement.utils.DateUtils;
import java.util.List;

/**
 * Repository for Meal operations
 */
public class MealRepository {
    private MessMateDatabase database;
    private String currentUserEmail;

    public MealRepository(MessMateDatabase database, String userEmail) {
        this.database = database;
        this.currentUserEmail = userEmail;
    }

    /**
     * Add new meal for a day
     */
    public void addMeal(String mealType, String date) {
        new Thread(() -> {
            Meal meal = new Meal(
                mealType,
                date,
                currentUserEmail,
                DateUtils.getCurrentDateTime()
            );
            database.mealDao().insertMeal(meal);
        }).start();
    }

    /**
     * Add meal entry (attendance)
     */
    public void addMealEntry(int mealId, int userId, String status) {
        new Thread(() -> {
            MealEntry entry = new MealEntry(
                mealId,
                userId,
                status,
                DateUtils.getCurrentDateTime()
            );
            database.mealEntryDao().insertMealEntry(entry);
        }).start();
    }

    /**
     * Get meals by month
     */
    public LiveData<List<Meal>> getMealsByMonth(String month) {
        return database.mealDao().getMealsByMonth(month);
    }

    /**
     * Get today's meals
     */
    public LiveData<List<Meal>> getTodaysMeals() {
        return database.mealDao().getTodaysMeals();
    }

    /**
     * Get all meals
     */
    public LiveData<List<Meal>> getAllMeals() {
        return database.mealDao().getAllMeals();
    }

    /**
     * Get meal count by month
     */
    public int getMealCountByMonth(String month) {
        try {
            return database.mealDao().getMealCountByMonth(month);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get total meals (all time)
     */
    public int getTotalMeals() {
        try {
            return database.mealDao().getTotalMeals();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Delete meal
     */
    public void deleteMeal(int mealId) {
        new Thread(() -> {
            Meal meal = database.mealDao().getMealById(mealId);
            if (meal != null) {
                database.mealDao().deleteMeal(meal);
            }
        }).start();
    }

    /**
     * Get meal entries by user and month
     */
    public LiveData<List<MealEntry>> getMealEntriesByUserAndMonth(int userId, String month) {
        return database.mealEntryDao().getMealEntriesByUserAndMonth(userId, month);
    }
}
