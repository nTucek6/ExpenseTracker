package com.example.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.enums.CrudActionEnum


@Entity(tableName = "summary_cache_crud",
    primaryKeys = ["year", "month"])
data class SummaryCacheCrud(
    val year: Int,
    val month: Int,
    val action: CrudActionEnum
)