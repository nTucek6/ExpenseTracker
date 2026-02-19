package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.entity.CacheCrud
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheCrudDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: CacheCrud): Long

    @Query("Delete from cache_crud")
    suspend fun deleteAll()

    @Query("Select * from cache_crud")
    fun getAllCrud(): Flow<List<CacheCrud>>
}