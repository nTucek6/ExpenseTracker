package com.example.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.enums.CrudActionEnum

@Entity(tableName = "cache_crud")
data class CacheCrud(
    @PrimaryKey
    val expenseId: Int,
    val action: CrudActionEnum
)
