package com.example.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.enums.ExpenseEnum
import java.util.Date

@Entity(tableName = "expenses")
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount : Double,
    val category : ExpenseEnum,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis()

)