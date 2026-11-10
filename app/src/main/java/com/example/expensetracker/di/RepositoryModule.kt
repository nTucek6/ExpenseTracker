package com.example.expensetracker.di

import com.example.expensetracker.data.repository.AnalyticsRepository
import com.example.expensetracker.data.repository.CategoriesRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.MonthlySummaryRepository
import com.example.expensetracker.data.repository.SyncRepository
import com.example.expensetracker.data.repository.impl.AnalyticsRepositoryImpl
import com.example.expensetracker.data.repository.impl.CategoriesRepositoryImpl
import com.example.expensetracker.data.repository.impl.ExpenseRepositoryImpl
import com.example.expensetracker.data.repository.impl.MonthlySummaryRepositoryImpl
import com.example.expensetracker.data.repository.impl.SyncRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindExpenseRepository(
        implementation: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    abstract fun bindSyncRepository(
        implementation: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    abstract fun bindCategoriesRepository(
        implementation: CategoriesRepositoryImpl
    ): CategoriesRepository

    @Binds
    abstract fun bindAnalyticsRepository(
        implementation: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    @Binds
    abstract fun bindMonthlySummaryRepository(
        implementation: MonthlySummaryRepositoryImpl
    ): MonthlySummaryRepository

}