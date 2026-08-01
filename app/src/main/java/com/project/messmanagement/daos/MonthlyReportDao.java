package com.project.messmanagement.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.project.messmanagement.entities.MonthlyReport;
import java.util.List;

/**
 * DAO for MonthlyReport entity
 */
@Dao
public interface MonthlyReportDao {
    @Insert
    long insertReport(MonthlyReport report);

    @Update
    int updateReport(MonthlyReport report);

    @Query("SELECT * FROM monthly_reports WHERE month = :month LIMIT 1")
    MonthlyReport getReportByMonth(String month);

    @Query("SELECT * FROM monthly_reports ORDER BY month DESC")
    LiveData<List<MonthlyReport>> getAllReports();

    @Query("SELECT * FROM monthly_reports ORDER BY month DESC LIMIT 1")
    MonthlyReport getLatestReport();

    @Query("SELECT COUNT(*) FROM monthly_reports")
    int getTotalReports();
}
