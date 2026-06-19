package com.example.expensetracker.data.model

data class ManageCategories(
    val id: Int = 0,
    val displayName: String,
    val imageSvg: Int,
    val isDefault: Boolean,
    val expensesCount: Int
)
