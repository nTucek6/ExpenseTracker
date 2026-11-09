package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val analyticsDao = ExpenseTrackerDatabase.getDatabase(application).analyticsDao()

    suspend fun getSummary(dateFrom: Long?, dateTo:Long?) = analyticsDao.getSummary(dateFrom,dateTo)

    suspend fun getTopCategorySpent(dateFrom: Long?, dateTo:Long?) = analyticsDao.getTopCategorySpent(dateFrom,dateTo)

}