package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.repository.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    application: Application,
    private val analyticsRepository: AnalyticsRepository
) : AndroidViewModel(application) {

    suspend fun getSummary(dateFrom: Long?, dateTo:Long?) = analyticsRepository.getSummary(dateFrom,dateTo)
    suspend fun getTopCategorySpent(dateFrom: Long?, dateTo:Long?) = analyticsRepository.getTopCategorySpent(dateFrom,dateTo)

}