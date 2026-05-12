package com.example.expensetracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.entity.SummaryCacheCrud
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryCacheCrudDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: SummaryCacheCrud): Long

    @Query("Delete from summary_cache_crud")
    suspend fun deleteAll()

    @Query("Select * from summary_cache_crud")
    fun getAllSummaryCrud(): Flow<List<SummaryCacheCrud>>
}