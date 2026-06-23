package com.example.expensetracker.firebase.database.mappers

import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseCategory
import com.example.expensetracker.firebase.database.model.FirebaseExpense
import com.example.expensetracker.firebase.database.model.FirebaseMonthlySummary

fun Expense.toFirebaseExpense() = FirebaseExpense(
    id = this.id.toString(),
    amount = this.amount,
    categoryId = this.categoryId,
    description = this.description,
    createdAt = this.createdAt,
    remoteId = this.remoteId,
)

fun List<Expense>.toFirebaseExpenses(): List<FirebaseExpense> = map { it.toFirebaseExpense() }
fun MonthlySummary.toFirebaseSummary() = FirebaseMonthlySummary(
    year = this.year,
    month = this.month,
    money = this.money
)

fun List<MonthlySummary>.toFirebaseMonthlySummary(): List<FirebaseMonthlySummary> =
    map { it.toFirebaseSummary() }


fun Categories.toFirebaseCategory() = FirebaseCategory(
    id = this.id.toString(),
    displayName = this.displayName,
    image = this.image,
    isDefault = this.isDefault,
    remoteId = this.remoteId
)

fun List<Categories>.toFirebaseCategories(): List<FirebaseCategory> = map {it.toFirebaseCategory()}

