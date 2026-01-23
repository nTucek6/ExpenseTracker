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
import com.example.expensetracker.data.model.ExpenseWithGroupSum

@Dao
interface ExpenseDao {

    @Query("SELECT * from expenses where id = :id")
    suspend fun findById(id:Int): Expense

    @Insert
    suspend fun insert(expense: Expense): Long
    @Update
    suspend fun update(expense: Expense) : Int
    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM expenses Order By createdAt DESC")
    fun getAllExpenses(): LiveData<List<Expense>>


    @Query("""
    SELECT *, 
           SUM(amount) OVER (
               PARTITION BY date(createdAt / 1000, 'unixepoch')
               ORDER BY createdAt
           ) AS dailySum
    FROM ExpenseWithGroupSum 
    WHERE (:query IS NULL OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%')
    ORDER BY createdAt DESC
""")
    fun getExpensesPaging(query: String? = null): PagingSource<Int, ExpenseWithGroupSum>

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC LIMIT 5")
    fun getRecentExpenses(): LiveData<List<Expense>>

    @Query("Select COALESCE(sum(amount), 0) from expenses")
    fun getTotalSpent(): LiveData<Double>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(expenses: List<Expense>)

}
