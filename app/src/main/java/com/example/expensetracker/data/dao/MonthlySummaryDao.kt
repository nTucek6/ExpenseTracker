package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.model.BudgetWithSpent

@Dao
interface MonthlySummaryDao {

    @Query("SELECT * from monthly_summary where year = :year AND month = :month")
    suspend fun findById(year: Int, month: Int): MonthlySummary

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: MonthlySummary)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(summary: MonthlySummary): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budget: List<MonthlySummary>)

    @Update
    suspend fun update(budget: MonthlySummary)

    @Query("SELECT * FROM monthly_summary WHERE year = :year AND month = :month")
    fun getBudget(year: Int, month: Int): LiveData<MonthlySummary?>

    @Query(
        """
    SELECT * FROM BudgetWithSpent 
    ORDER BY year DESC, month DESC 
    LIMIT 1
"""
    )
    fun getCurrentMonthBudget(): LiveData<BudgetWithSpent>

    @Query(
        """
    SELECT * FROM BudgetWithSpent
    WHERE (year * 100 + month) BETWEEN (:startYear * 100 + :startMonth)
                                  AND (:endYear * 100 + :endMonth)
"""
    )
   fun getRangeMonthBudget(
        startYear: Int,
        startMonth: Int,
        endYear: Int,
        endMonth: Int
    ): LiveData<List<BudgetWithSpent>>

    @Query("SELECT * FROM monthly_summary")
    fun getAllMonthBudget(): LiveData<List<MonthlySummary>>

    @Query(
        """
    SELECT * FROM BudgetWithSpent 
    WHERE (:queryYear IS 0 OR :queryYear == year) AND (:queryMonth is 0 OR :queryMonth == month)
    ORDER BY year DESC, month DESC
"""
    )
    fun getMonthBudgetPaging(
        queryYear: Int? = 0,
        queryMonth: Int? = 0
    ): PagingSource<Int, BudgetWithSpent>

    @Query("SELECT COUNT(*) FROM monthly_summary WHERE year = :year AND month = :month")
    suspend fun budgetExists(year: Int, month: Int): Int

    @Query("Select DISTINCT m.year from monthly_summary m Order By m.year Desc")
    suspend fun findAllExistingYears(): List<Int>

    @Transaction
    suspend fun insertOrUpdateIfNewer(summary: MonthlySummary): Boolean {
        val inserted = insertIgnore(summary)
        if (inserted != -1L) {
            return true
        }

        val updated = updateIfNewer(
            year = summary.year,
            month = summary.month,
            money = summary.money,
            updatedAt = summary.updatedAt
        )
        return updated > 0
    }

    @Query("""
    UPDATE monthly_summary
    SET money = :money,
        updatedAt = :updatedAt
    WHERE year = :year AND month = :month
      AND updatedAt < :updatedAt
""")
    suspend fun updateIfNewer(
        year: Int,
        month: Int,
        money: Double,
        updatedAt: Long
    ): Int

}