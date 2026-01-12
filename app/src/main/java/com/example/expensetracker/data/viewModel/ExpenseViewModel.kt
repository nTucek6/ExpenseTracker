package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Expense
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseTrackerDatabase.getDatabase(application).expenseDao()
    val allExpenses = expenseDao.getAllExpenses();

    fun insert(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insert(expense)
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
}