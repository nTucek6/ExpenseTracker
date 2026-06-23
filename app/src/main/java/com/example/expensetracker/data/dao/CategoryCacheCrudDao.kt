package com.example.expensetracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.entity.CategoryCacheCrud
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryCacheCrudDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: CategoryCacheCrud): Long

    @Query("Delete from category_cache_crud")
    suspend fun deleteAll()

    @Query("Select * from category_cache_crud")
    fun getAllCrud(): Flow<List<CategoryCacheCrud>>
}