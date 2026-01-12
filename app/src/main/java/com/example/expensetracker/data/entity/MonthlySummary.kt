package com.example.expensetracker.data.entity

import androidx.room.Entity

@Entity(tableName = "monthly_summary", primaryKeys = ["year", "month"])
data class MonthlySummary(
    val year: Int,
    val month: Int,
    val money: Double,
)
