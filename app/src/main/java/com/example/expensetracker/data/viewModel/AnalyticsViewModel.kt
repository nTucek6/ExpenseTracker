package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.expensetracker.data.database.ExpenseTrackerDatabase

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val analyticsDao = ExpenseTrackerDatabase.getDatabase(application).analyticsDao()

    suspend fun getSummary(dateFrom: Long?, dateTo:Long?) = analyticsDao.getSummary(dateFrom,dateTo)

    suspend fun getTopCategorySpent(dateFrom: Long?, dateTo:Long?) = analyticsDao.getTopCategorySpent(dateFrom,dateTo)

}