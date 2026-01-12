package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.model.BudgetWithSpent

@Dao
interface MonthlySummaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: MonthlySummary)

    @Update
    suspend fun update(budget: MonthlySummary)

    @Query("SELECT * FROM monthly_summary WHERE year = :year AND month = :month")
    fun getBudget(year: Int, month: Int): LiveData<MonthlySummary?>

    @Query("SELECT * FROM BudgetWithSpent")
    fun getCurrentMonthBudget(): LiveData<BudgetWithSpent>

    @Query("SELECT COUNT(*) FROM monthly_summary WHERE year = :year AND month = :month")
    suspend fun budgetExists(year: Int, month: Int): Int
}