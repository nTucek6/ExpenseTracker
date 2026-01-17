package com.example.expensetracker.firebase.database.mappers

import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseExpense
import com.example.expensetracker.firebase.database.model.FirebaseMonthlySummary

fun Expense.toFirebaseExpense() = FirebaseExpense(
    id = this.id.toString(),
    amount = this.amount,
    category = this.category.displayName,
    description = this.description,
    createdAt = this.createdAt
)

fun List<Expense>.toFirebaseExpenses(): List<FirebaseExpense> = map { it.toFirebaseExpense() }
fun MonthlySummary.toFirebaseSummary() = FirebaseMonthlySummary(
    year = this.year,
    month = this.month,
    money = this.money
)

fun List<MonthlySummary>.toFirebaseMonthlySummary(): List<FirebaseMonthlySummary> =
    map { it.toFirebaseSummary() }

