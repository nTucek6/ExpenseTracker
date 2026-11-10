package com.example.expensetracker.data.repository.impl

import com.example.expensetracker.data.dao.AnalyticsDao
import com.example.expensetracker.data.model.AnalyticsSummary
import com.example.expensetracker.data.model.TopCategorySpent
import com.example.expensetracker.data.repository.AnalyticsRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao
) : AnalyticsRepository {

    override suspend fun getSummary(dateFrom: Long?, dateTo: Long?): AnalyticsSummary {
        return analyticsDao.getSummary(dateFrom,dateTo)
    }
    override suspend fun getTopCategorySpent(dateFrom: Long?, dateTo: Long?): List<TopCategorySpent> {
        return analyticsDao.getTopCategorySpent(dateFrom,dateTo)
    }
}