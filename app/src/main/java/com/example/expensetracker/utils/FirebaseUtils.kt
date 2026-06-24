package com.example.expensetracker.utils

import com.example.expensetracker.data.entity.Expense
import com.google.firebase.database.DataSnapshot

object FirebaseUtils {


    fun snapshotToExpense(snapshot: DataSnapshot): Expense{
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: ""
        val amount = snapshot.child("amount").getValue(Double::class.java) ?: 0.0
        val updatedAt = snapshot.child("updatedAt").getValue(Long::class.java) ?: 0L
        val categoryId = snapshot.child("categoryId").getValue(String::class.java) ?: ""
        val description = snapshot.child("description").getValue(String::class.java) ?: ""
        val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L

        return Expense(
            id = id,
            categoryId = categoryId,
            amount = amount,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

}