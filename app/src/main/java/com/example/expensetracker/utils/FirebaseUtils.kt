package com.example.expensetracker.utils

import android.util.Log
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.enums.CategoryIconEnum
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

    fun snapshotToCategory(snapshot: DataSnapshot): Categories{
        val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: ""
        val displayName = snapshot.child("displayName").getValue(String::class.java) ?: ""
        val imageKey = snapshot.child("image").getValue(String::class.java) ?: ""
        val isDefault = snapshot.child("isDefault").getValue(Boolean::class.java) ?: false
        val updatedAt = snapshot.child("updatedAt").getValue(Long::class.java) ?: 0L

        val image = CategoryIconEnum.entries.firstOrNull { it.key == imageKey.lowercase() }
            ?: CategoryIconEnum.OTHER

        return Categories(
            id = id,
            displayName = displayName,
            image = image,
            isDefault= isDefault,
            updatedAt = updatedAt
        )
    }

    fun snapshotToSummary(snapshot: DataSnapshot): MonthlySummary{
        val year = snapshot.child("year").getValue(Int::class.java) ?: 0
        val month = snapshot.child("month").getValue(Int::class.java) ?: 0
        val money = snapshot.child("money").getValue(Double::class.java) ?: 0.0
        val updatedAt = snapshot.child("updatedAt").getValue(Long::class.java) ?: 0L

        return MonthlySummary(
            year = year,
            month = month,
            money = money,
            updatedAt = updatedAt
        )
    }

}