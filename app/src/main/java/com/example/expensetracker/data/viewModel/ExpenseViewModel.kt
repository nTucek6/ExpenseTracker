package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.ExpenseEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseTrackerDatabase.getDatabase(application).expenseDao()
    val allExpenses = expenseDao.getAllExpenses()
    val totalSpent = expenseDao.getTotalSpent()
    val recentExpenses = expenseDao.getRecentExpenses()

    fun insert(amount: Double, description: String?, category: ExpenseEnum) {
        viewModelScope.launch {
            expenseDao.insert(Expense(amount = amount,description = description,category = category))
        }
    }

    fun update(expense: Expense) {
        viewModelScope.launch {
            expenseDao.update(expense)
        }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch {
            expenseDao.delete(expense)
        }
    }

    fun getMonthlySummary(): Flow<PagingData<Expense>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { expenseDao.getAllExpensesPaging() }
        ).flow
            .cachedIn(viewModelScope)  // Cache across config changes
    }

}