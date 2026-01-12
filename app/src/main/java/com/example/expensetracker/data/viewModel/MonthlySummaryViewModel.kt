package com.example.expensetracker.data.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import kotlinx.coroutines.launch
import java.util.Calendar

class MonthlySummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val monthlySummaryDao =
        ExpenseTrackerDatabase.getDatabase(application).monthlySummaryDao()

    val getCurrentMonthBudget = monthlySummaryDao.getCurrentMonthBudget()

    fun getBudget(year: Int, month: Int): LiveData<MonthlySummary?> {
        return monthlySummaryDao.getBudget(year, month)
    }

    fun insertSummary(summary: MonthlySummary) {
        viewModelScope.launch {
            monthlySummaryDao.insert(summary)
        }
    }

    fun updateSummary(summary: MonthlySummary) {
        viewModelScope.launch {
            monthlySummaryDao.update(summary)
        }
    }

    fun createDefaultSummary() {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH) + 1

            val data = monthlySummaryDao.budgetExists(year, month)
            Log.d("Logger", "Start")
            if (data == 0) {
                val defaultBudget = MonthlySummary(
                    year = year,
                    month = month,
                    money = 0.0
                )
                monthlySummaryDao.insert(defaultBudget)
            }
        }
    }

}