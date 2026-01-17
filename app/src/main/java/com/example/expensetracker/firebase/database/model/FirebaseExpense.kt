package com.example.expensetracker.firebase.database.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseExpense(
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String? = null,
    val createdAt: Long = 0L
)
