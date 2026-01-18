package com.example.expensetracker.data.model

import androidx.room.DatabaseView
import com.example.expensetracker.data.enums.ExpenseEnum

@DatabaseView("""
    SELECT * FROM expenses ORDER BY createdAt DESC
""")
data class ExpenseWithGroupSum(
    val id: Int = 0,
    val amount : Double,
    val category : ExpenseEnum,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis(),
    val dailySum: Double
)
