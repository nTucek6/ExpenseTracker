package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.model.BudgetWithSpent

@Dao
interface MonthlySummaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: MonthlySummary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budget: List<MonthlySummary>)

    @Update
    suspend fun update(budget: MonthlySummary)

    @Query("SELECT * FROM monthly_summary WHERE year = :year AND month = :month")
    fun getBudget(year: Int, month: Int): LiveData<MonthlySummary?>

    @Query("SELECT * FROM BudgetWithSpent")
    fun getCurrentMonthBudget(): LiveData<BudgetWithSpent>

    @Query("SELECT * FROM monthly_summary")
    fun getAllMonthBudget(): LiveData<List<MonthlySummary>>

    @Query("""
    SELECT * FROM BudgetWithSpent 
    WHERE (:queryYear IS 0 OR :queryYear == year) AND (:queryMonth is 0 OR :queryMonth == month)
    ORDER BY year and month DESC
""")
    fun getMonthBudgetPaging(queryYear: Int? = 0, queryMonth: Int? = 0): PagingSource<Int, BudgetWithSpent>

    @Query("SELECT COUNT(*) FROM monthly_summary WHERE year = :year AND month = :month")
    suspend fun budgetExists(year: Int, month: Int): Int

    @Query("Select DISTINCT m.year from monthly_summary m Order By m.year Desc")
    suspend fun findAllExistingYears() : List<Int>


}