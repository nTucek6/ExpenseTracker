package com.example.expensetracker.firebase.database.model

import com.example.expensetracker.data.entity.Expense
import com.google.firebase.database.IgnoreExtraProperties
import java.util.UUID

@IgnoreExtraProperties
data class FirebaseExpense(
    val id: String = "",
    val amount: Double = 0.0,
    val categoryId: String = "",
    val description: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long,
    //val remoteId: String = UUID.randomUUID().toString(),
) {
    fun toExpense(): Expense = Expense(
        id = id,
        amount = amount,
        categoryId = categoryId,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
