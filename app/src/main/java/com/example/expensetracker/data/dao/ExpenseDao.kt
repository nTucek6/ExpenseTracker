package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.entity.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(task: Expense)
    @Update
    suspend fun update(task: Expense)
    @Delete
    suspend fun delete(task: Expense)
    @Query("SELECT * FROM expenses Order By createdAt DESC")
    fun getAllExpenses(): LiveData<List<Expense>>
}
