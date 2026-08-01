package com.project.messmanagement.repositories;

import androidx.lifecycle.LiveData;
import com.project.messmanagement.MessMateDatabase;
import com.project.messmanagement.entities.Bazar;
import com.project.messmanagement.utils.DateUtils;
import java.util.List;

/**
 * Repository for Bazar operations
 */
public class BazarRepository {
    private MessMateDatabase database;
    private String currentUserEmail;

    public BazarRepository(MessMateDatabase database, String userEmail) {
        this.database = database;
        this.currentUserEmail = userEmail;
    }

    /**
     * Add new bazar item
     */
    public void addBazarItem(String itemName, double amount, String category,
                            String date, String description) {
        new Thread(() -> {
            Bazar bazar = new Bazar(
                itemName,
                amount,
                date,
                category,
                currentUserEmail,
                description,
                DateUtils.getCurrentDateTime()
            );
            database.bazarDao().insertBazar(bazar);
        }).start();
    }

    /**
     * Get all bazar items
     */
    public LiveData<List<Bazar>> getAllBazarItems() {
        return database.bazarDao().getAllBazarItems();
    }

    /**
     * Get bazar items by month
     */
    public LiveData<List<Bazar>> getBazarByMonth(String month) {
        return database.bazarDao().getBazarByMonth(month);
    }

    /**
     * Get bazar total for a month (for sync call)
     */
    public double getBazarTotalByMonth(String month) {
        try {
            return database.bazarDao().getBazarTotalByMonth(month);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get recent bazar items
     */
    public LiveData<List<Bazar>> getRecentBazarItems(int limit) {
        return database.bazarDao().getRecentBazarItems(limit);
    }

    /**
     * Delete bazar item
     */
    public void deleteBazarItem(int bazarId) {
        new Thread(() -> {
            Bazar bazar = database.bazarDao().getBazarById(bazarId);
            if (bazar != null) {
                database.bazarDao().deleteBazar(bazar);
            }
        }).start();
    }

    /**
     * Update bazar item
     */
    public void updateBazarItem(int bazarId, String itemName, double amount) {
        new Thread(() -> {
            Bazar bazar = database.bazarDao().getBazarById(bazarId);
            if (bazar != null) {
                bazar.itemName = itemName;
                bazar.amount = amount;
                database.bazarDao().updateBazar(bazar);
            }
        }).start();
    }

    /**
     * Get total bazar count
     */
    public int getTotalBazarItems() {
        try {
            return database.bazarDao().getTotalBazarItems();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get total bazar amount
     */
    public double getTotalBazarAmount() {
        try {
            return database.bazarDao().getTotalBazarAmount();
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get current month bazar count
     */
    public int getCurrentMonthBazarCount() {
        try {
            String currentMonth = DateUtils.getCurrentMonth();
            return database.bazarDao().getBazarCountByMonth(currentMonth);
        } catch (Exception e) {
            return 0;
        }
    }
}
