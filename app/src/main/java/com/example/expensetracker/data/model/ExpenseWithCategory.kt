package com.example.expensetracker.data.model

import androidx.room.DatabaseView

@DatabaseView("""
    SELECT e.*, c.displayName AS categoryName, c.imageSvg AS imageSvg
    FROM expenses e
    LEFT JOIN categories c ON e.categoryId = c.id
    ORDER BY e.id DESC
""")
data class ExpenseWithCategory(
    val id: Int = 0,
    val amount : Double,
    val categoryId : Int,
    val categoryName: String,
    val imageSvg: String,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis(),
)
