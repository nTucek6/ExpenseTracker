package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.ExpenseEnum
import com.example.expensetracker.utils.toDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseTrackerDatabase.getDatabase(application).expenseDao()
    val allExpenses = expenseDao.getAllExpenses()
    val totalSpent = expenseDao.getTotalSpent()
    val recentExpenses = expenseDao.getRecentExpenses()

    private val _query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesPaging: Flow<PagingData<Expense>> = _query
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 15,  prefetchDistance = 5, enablePlaceholders = false),
                pagingSourceFactory = { expenseDao.getExpensesPaging(query) }
            ).flow.cachedIn(viewModelScope)
        }
    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun insert(amount: Double, description: String?, category: ExpenseEnum) {
        viewModelScope.launch {
            expenseDao.insert(
                Expense(
                    amount = amount,
                    description = description,
                    category = category
                )
            )
        }
    }

    fun update(
        id: Int,
        amount: Double,
        description: String?,
        category: ExpenseEnum,
        createdAt: Long
    ) {
        viewModelScope.launch {
            expenseDao.update(
                Expense(
                    id = id,
                    amount = amount,
                    description = description,
                    category = category,
                    createdAt = createdAt
                )
            )
        }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch {
            expenseDao.delete(expense)
        }
    }
}