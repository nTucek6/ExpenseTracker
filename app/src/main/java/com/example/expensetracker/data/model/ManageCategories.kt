package com.example.expensetracker.data.model

import com.example.expensetracker.data.enums.CategoryIconEnum

data class ManageCategories(
    val id: Int = 0,
    val displayName: String,
    val image: CategoryIconEnum,
    val isDefault: Boolean,
    val expensesCount: Int
)
