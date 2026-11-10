package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.AnalyticsSummary
import com.example.expensetracker.data.model.TopCategorySpent

interface AnalyticsRepository {

    suspend fun getSummary(dateFrom: Long?, dateTo: Long?): AnalyticsSummary
    suspend fun getTopCategorySpent(dateFrom : Long?, dateTo : Long?): List<TopCategorySpent>
}