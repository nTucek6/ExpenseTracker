package com.example.expensetracker.data.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.model.BudgetWithSpent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class MonthlySummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val monthlySummaryDao =
        ExpenseTrackerDatabase.getDatabase(application).monthlySummaryDao()

    val getCurrentMonthBudget = monthlySummaryDao.getCurrentMonthBudget()

    val findAllExistingYears= monthlySummaryDao.findAllExistingYears()

    private val _query = MutableStateFlow("")

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

    fun updateLatestMonth(limit: Double) {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH) + 1

            monthlySummaryDao.update(MonthlySummary(year = year, month = month, money = limit))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val summaryPaging: Flow<PagingData<BudgetWithSpent>> = _query
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 15,  prefetchDistance = 5, enablePlaceholders = false),
                pagingSourceFactory = { monthlySummaryDao.getMonthBudgetPaging(query) }
            ).flow.cachedIn(viewModelScope)
        }


}