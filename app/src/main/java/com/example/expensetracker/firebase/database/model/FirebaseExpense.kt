package com.example.expensetracker.firebase.database.model

import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.ExpenseEnum
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseExpense(
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String? = null,
    val createdAt: Long = 0L
) {
    constructor() : this("", 0.0, "", null, 0L)

    fun toExpense(): Expense = Expense(
        id = id.toIntOrNull() ?: 0,
        amount = amount,
        category = ExpenseEnum.valueOf(category.uppercase()),
        description = description,
        createdAt = createdAt
    )
}
