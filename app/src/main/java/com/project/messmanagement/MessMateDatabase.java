package com.project.messmanagement;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.project.messmanagement.daos.BazarDao;
import com.project.messmanagement.daos.CashTransactionDao;
import com.project.messmanagement.daos.ExpenseDao;
import com.project.messmanagement.daos.MealDao;
import com.project.messmanagement.daos.MealEntryDao;
import com.project.messmanagement.daos.MealRateDao;
import com.project.messmanagement.daos.MonthlyReportDao;
import com.project.messmanagement.entities.Bazar;
import com.project.messmanagement.entities.CashTransaction;
import com.project.messmanagement.entities.Expense;
import com.project.messmanagement.entities.Meal;
import com.project.messmanagement.entities.MealEntry;
import com.project.messmanagement.entities.MealRate;
import com.project.messmanagement.entities.MonthlyReport;

/**
 * Room Database for MessMate app
 * Handles all database operations for 8 entities
 * Version: 1
 */
@Database(
    entities = {
        Meal.class,
        MealEntry.class,
        Bazar.class,
        CashTransaction.class,
        Expense.class,
        MealRate.class,
        MonthlyReport.class
    },
    version = 1,
    exportSchema = false
)
public abstract class MessMateDatabase extends RoomDatabase {

    // Abstract methods for DAOs
    public abstract MealDao mealDao();
    public abstract MealEntryDao mealEntryDao();
    public abstract BazarDao bazarDao();
    public abstract CashTransactionDao cashTransactionDao();
    public abstract ExpenseDao expenseDao();
    public abstract MealRateDao mealRateDao();
    public abstract MonthlyReportDao monthlyReportDao();

    // Singleton instance
    private static volatile MessMateDatabase INSTANCE;

    /**
     * Get database instance (singleton pattern)
     */
    public static MessMateDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (MessMateDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MessMateDatabase.class,
                            "messmate_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Close database connection (optional)
     */
    public static void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}
