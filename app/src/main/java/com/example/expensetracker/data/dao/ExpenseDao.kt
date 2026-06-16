package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.model.ExpenseWithCategory
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import com.example.expensetracker.data.model.DailyBudgetSpent
import com.example.expensetracker.data.model.SpentPerCategory
import com.example.expensetracker.data.model.WeeklyBudgetSpent

@Dao
interface ExpenseDao {

    @Query("SELECT * from expenses where id = :id")
    suspend fun findById(id: Int): Expense

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense): Int

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM expenses Order By createdAt DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query(
        """
    SELECT *,
           SUM(amount) OVER (
               PARTITION BY date(createdAt / 1000, 'unixepoch', 'localtime')
               ORDER BY createdAt
           ) AS dailySum
    FROM ExpenseWithGroupSum
    WHERE (:query IS NULL OR description LIKE '%' || :query || '%' OR categoryName LIKE '%' || :query || '%')
    ORDER BY createdAt DESC
"""
    )
    fun getExpensesPaging(query: String? = null): PagingSource<Int, ExpenseWithGroupSum>

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC LIMIT 5")
    fun getRecentExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM ExpenseWithCategory ORDER BY createdAt DESC LIMIT 5")
    fun getRecentExpensesWithCategory(): LiveData<List<ExpenseWithCategory>>

    @Query("Select COALESCE(sum(amount), 0) from expenses")
    fun getTotalSpent(): LiveData<Double>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(expenses: List<Expense>)

    @Query(
        """
    SELECT * FROM DailyBudgetSpent s
    WHERE (:dateFrom IS NULL OR s.date >= :dateFrom)
      AND (:dateTo IS NULL OR s.date <= :dateTo)
"""
    )
    suspend fun getDailyBudgetSpent(dateFrom: Long?, dateTo: Long?): List<DailyBudgetSpent>

    @Query(
        """
            WITH weeks AS (
    SELECT
        createdAt,
        amount,
        (
            (JULIANDAY(
                DATE(createdAt / 1000, 'unixepoch', 'localtime', 'weekday 1')
            ) - 2440587.5) * 86400
        ) * 1000 AS weekStartDate,
        (
            (JULIANDAY(
                DATE(createdAt / 1000, 'unixepoch', 'localtime', 'weekday 1', '+6 days')
            ) - 2440587.5) * 86400
        ) * 1000 AS weekEndDate
    FROM expenses
    WHERE createdAt >= :dateFrom
      AND createdAt < :dateTo
)
SELECT
    weekStartDate,
    weekEndDate,
    SUM(amount) AS total
FROM weeks
GROUP BY weekStartDate
ORDER BY weekStartDate;
        """
    )
    suspend fun getWeeklyBudgetSpent(dateFrom: Long?, dateTo: Long?) : List<WeeklyBudgetSpent>

    @Query(
        """
        SELECT 
            SUM(e.amount) AS amount,
            e.categoryId AS categoryId,
            c.displayName AS category
        FROM expenses e
        LEFT JOIN categories c ON e.categoryId = c.id
        WHERE (:dateFrom IS NULL OR e.createdAt >= :dateFrom)
        AND (:dateTo IS NULL OR e.createdAt <= :dateTo)
        GROUP BY e.categoryId, c.displayName
        ORDER BY amount DESC
    """
    )
    suspend fun getSpentPerCategory(dateFrom: Long?, dateTo: Long?): List<SpentPerCategory>
}
