package com.example.expensetracker.data.repository.impl

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.example.expensetracker.data.dao.CacheCrudDao
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.model.DailyBudgetSpent
import com.example.expensetracker.data.model.ExpenseWithCategory
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import com.example.expensetracker.data.model.MonthlyBudgetSpent
import com.example.expensetracker.data.model.SpentPerCategory
import com.example.expensetracker.data.model.WeeklyBudgetSpent
import com.example.expensetracker.data.repository.ExpenseRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val cacheDao : CacheCrudDao) : ExpenseRepository {

    override suspend fun findById(id: String): Expense {
        return expenseDao.findById(id)
    }

    override suspend fun insert(expense: Expense): Long {
        return expenseDao.insert(expense)
    }

    override suspend fun update(expense: Expense): Int {
        return expenseDao.update(expense)
    }

    override suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    override suspend fun deleteAll() {
        expenseDao.deleteAll()
    }

    override fun getAllExpenses(): LiveData<List<Expense>> {
        return expenseDao.getAllExpenses()
    }

    override fun getTotalSpent(): LiveData<Double> {
        return expenseDao.getTotalSpent()
    }

    override fun getRecentExpensesWithCategory(): LiveData<List<ExpenseWithCategory>> {
        return expenseDao.getRecentExpensesWithCategory()
    }

    override suspend fun getDailyBudgetSpent(
        dateFrom: Long?,
        dateTo: Long?
    ): List<DailyBudgetSpent> {
        return expenseDao.getDailyBudgetSpent(dateFrom, dateTo)
    }

    override suspend fun getWeeklyBudgetSpent(
        dateFrom: Long?,
        dateTo: Long?
    ): List<WeeklyBudgetSpent> {
        return expenseDao.getWeeklyBudgetSpent(dateFrom, dateTo)
    }

    override suspend fun getMonthlyBudgetSpent(
        dateFrom: Long?,
        dateTo: Long?
    ): List<MonthlyBudgetSpent> {
        return expenseDao.getMonthlyBudgetSpent(dateFrom, dateTo)
    }

    override suspend fun getSpentPerCategory(
        dateFrom: Long?,
        dateTo: Long?
    ): List<SpentPerCategory> {
        return expenseDao.getSpentPerCategory(dateFrom, dateTo)
    }

    override suspend fun replaceAll(expenses: List<Expense>) {
        expenseDao.replaceAll(expenses)
    }

    override fun getExpensesPaging(query: String?): PagingSource<Int, ExpenseWithGroupSum> {
        return expenseDao.getExpensesPaging(query)
    }


}