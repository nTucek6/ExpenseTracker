package com.example.expensetracker.data.model

import androidx.room.DatabaseView


data class SpentPerCategory(
    val amount: Double,
    val categoryId: Int,
    val category: String?,
)
