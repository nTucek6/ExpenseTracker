package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.entity.Categories

@Dao
interface CategoriesDao {

    @Insert
    suspend fun insert(categories: Categories): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Categories>)
   /* @Delete
    suspend fun delete(id: Long) : Int*/
    @Update
    suspend fun update(categories: Categories): Int

    @Query("SELECT * FROM categories ")
    fun getAllCategories(): LiveData<List<Categories>>

}