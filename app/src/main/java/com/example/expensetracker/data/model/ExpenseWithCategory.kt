package com.example.expensetracker.data.model

import androidx.room.DatabaseView
import com.example.expensetracker.data.enums.ExpenseEnum


@DatabaseView("""
    SELECT e.id, e.amount, e.description, e.categoryId, c.displayName AS categoryName
    FROM expenses e
    INNER JOIN categories c ON e.categoryId = c.id
    ORDER BY e.id DESC
""")
data class ExpenseWithCategory(
    val id: Int = 0,
    val amount : Double,
    val category : ExpenseEnum,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis(),
    val dailySum: Double
)
