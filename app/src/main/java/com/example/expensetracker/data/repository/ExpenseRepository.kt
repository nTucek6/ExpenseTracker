package com.example.expensetracker.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.model.DailyBudgetSpent
import com.example.expensetracker.data.model.ExpenseWithCategory
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import com.example.expensetracker.data.model.MonthlyBudgetSpent
import com.example.expensetracker.data.model.SpentPerCategory
import com.example.expensetracker.data.model.WeeklyBudgetSpent

interface ExpenseRepository {

    suspend fun findById(id: String): Expense

    suspend fun insert(expense: Expense): Long

    suspend fun update(expense: Expense): Int

    suspend fun delete(expense: Expense)

    suspend fun deleteAll()

    fun getAllExpenses() : LiveData<List<Expense>>
    fun getTotalSpent() : LiveData<Double>

    fun getRecentExpensesWithCategory() : LiveData<List<ExpenseWithCategory>>
    suspend fun getDailyBudgetSpent(dateFrom: Long?, dateTo: Long?): List<DailyBudgetSpent>
    suspend fun getWeeklyBudgetSpent(dateFrom: Long?, dateTo: Long?): List<WeeklyBudgetSpent>
    suspend fun getMonthlyBudgetSpent(dateFrom: Long?, dateTo: Long?): List<MonthlyBudgetSpent>
    suspend fun getSpentPerCategory(dateFrom: Long?, dateTo: Long?): List<SpentPerCategory>

    suspend fun replaceAll(expenses: List<Expense>)

    fun getExpensesPaging(query: String? = null): PagingSource<Int, ExpenseWithGroupSum>


}