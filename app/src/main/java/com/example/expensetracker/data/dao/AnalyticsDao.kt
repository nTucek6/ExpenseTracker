package com.example.expensetracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.expensetracker.data.model.AnalyticsSummary
import com.example.expensetracker.data.model.TopCategorySpent

@Dao
interface AnalyticsDao {

    @Query("""
        SELECT
            COALESCE(SUM(e.amount), 0) AS totalSpent,
            COALESCE(AVG(e.amount), 0) AS avgPerDay
        FROM expenses e
        WHERE (:startDate IS NULL OR createdAt >= :startDate)
          AND (:endDate IS NULL OR createdAt <= :endDate)
    """)
    suspend fun getSummary(startDate: Long?, endDate: Long?): AnalyticsSummary

    @Query("""
        SELECT
        c.id AS categoryId,
        c.displayName AS categoryName,
        COALESCE(SUM(e.amount), 0) AS totalSpent
        FROM expenses e
        JOIN categories c ON e.categoryId = c.id
        WHERE (:startDate IS NULL OR createdAt >= :startDate)
        AND (:endDate IS NULL OR createdAt <= :endDate)
        GROUP BY e.categoryId, c.displayName
        ORDER BY totalSpent DESC
        LIMIT 3;
    """)
    suspend fun getTopCategorySpent(startDate: Long?, endDate: Long?): List<TopCategorySpent>


}