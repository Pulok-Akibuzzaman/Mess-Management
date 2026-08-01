package com.project.messmanagement.repositories;

import androidx.lifecycle.LiveData;
import com.project.messmanagement.MessMateDatabase;
import com.project.messmanagement.entities.MonthlyReport;
import com.project.messmanagement.utils.DateUtils;
import java.util.List;

/**
 * Repository for Report operations
 */
public class ReportRepository {
    private MessMateDatabase database;
    private CashRepository cashRepository;
    private BazarRepository bazarRepository;
    private MealRepository mealRepository;

    public ReportRepository(MessMateDatabase database, String userEmail) {
        this.database = database;
        this.cashRepository = new CashRepository(database, userEmail);
        this.bazarRepository = new BazarRepository(database, userEmail);
        this.mealRepository = new MealRepository(database, userEmail);
    }

    /**
     * Generate monthly report (calculates stats and saves)
     */
    public void generateMonthlyReport(String month) {
        new Thread(() -> {
            try {
                double totalIncome = cashRepository.getTotalIncomeByMonth(month);
                double totalExpense = cashRepository.getTotalExpenseByMonth(month);
                int totalMeals = mealRepository.getMealCountByMonth(month);

                // Assuming 5 members for average calculation
                double averagePerPerson = totalExpense > 0 ? totalExpense / 5 : 0;
                double mealRatePerPerson = totalExpense > 0 ? totalExpense / (totalMeals > 0 ? totalMeals : 1) : 0;

                MonthlyReport report = new MonthlyReport(
                    month,
                    totalIncome,
                    totalExpense,
                    totalMeals,
                    averagePerPerson,
                    mealRatePerPerson,
                    DateUtils.getCurrentDateTime()
                );

                database.monthlyReportDao().insertReport(report);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Get report by month
     */
    public MonthlyReport getReportByMonth(String month) {
        try {
            return database.monthlyReportDao().getReportByMonth(month);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get all reports
     */
    public LiveData<List<MonthlyReport>> getAllReports() {
        return database.monthlyReportDao().getAllReports();
    }

    /**
     * Get latest report
     */
    public MonthlyReport getLatestReport() {
        try {
            return database.monthlyReportDao().getLatestReport();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get current month report
     */
    public MonthlyReport getCurrentMonthReport() {
        try {
            String currentMonth = DateUtils.getCurrentMonth();
            MonthlyReport report = getReportByMonth(currentMonth);

            // If report doesn't exist, generate it
            if (report == null) {
                generateMonthlyReport(currentMonth);
                // Wait a moment for generation
                Thread.sleep(500);
                report = getReportByMonth(currentMonth);
            }

            return report;
        } catch (Exception e) {
            return null;
        }
    }
}
