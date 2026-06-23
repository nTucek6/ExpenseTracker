package com.example.expensetracker.data.model

import androidx.room.DatabaseView
import com.example.expensetracker.data.enums.CategoryIconEnum

//COALESCE(c.imageSvg, 0) AS imageSvg
@DatabaseView("""
    SELECT e.*, 
    COALESCE(c.displayName, 'No category') AS categoryName,
    c.image
    FROM expenses e 
    LEFT JOIN categories c ON e.categoryId = c.id ORDER BY createdAt DESC
""")
data class ExpenseWithGroupSum(
    val id: String,
    val amount : Double,
    val categoryId : String,
    val categoryName: String,
    val image: CategoryIconEnum,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis(),
    val dailySum: Double,
)
