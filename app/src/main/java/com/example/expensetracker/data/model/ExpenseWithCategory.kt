package com.example.expensetracker.data.model

import androidx.room.DatabaseView

@DatabaseView("""
    SELECT e.*, 
    COALESCE(c.displayName, 'No category') AS categoryName,
    COALESCE(c.imageSvg, 0) AS imageSvg
    FROM expenses e
    LEFT JOIN categories c ON e.categoryId = c.id
    ORDER BY e.id DESC
""")
data class ExpenseWithCategory(
    val id: Int = 0,
    val amount : Double,
    val categoryId : Int,
    val categoryName: String,
    val imageSvg: Int,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis(),
)
