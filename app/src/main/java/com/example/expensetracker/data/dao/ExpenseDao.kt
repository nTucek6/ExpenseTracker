package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.entity.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)
    @Update
    suspend fun update(expense: Expense)
    @Delete
    suspend fun delete(expense: Expense)
    @Query("SELECT * FROM expenses Order By createdAt DESC")
    fun getAllExpenses(): LiveData<List<Expense>>


    @Query("""
    SELECT * FROM expenses 
    WHERE (:query IS NULL OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%') 
    ORDER BY createdAt DESC
""")
    fun getExpensesPaging(query: String? = null): PagingSource<Int, Expense>

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC LIMIT 5")
    fun getRecentExpenses(): LiveData<List<Expense>>

    @Query("Select COALESCE(sum(amount), 0) from expenses")
    fun getTotalSpent(): LiveData<Double>

}
