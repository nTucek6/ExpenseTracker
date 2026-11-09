package com.example.expensetracker.di

import android.app.Application
import com.example.expensetracker.data.dao.CacheCrudDao
import com.example.expensetracker.data.dao.CategoriesDao
import com.example.expensetracker.data.dao.CategoryCacheCrudDao
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.dao.MonthlySummaryDao
import com.example.expensetracker.data.dao.SummaryCacheCrudDao
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Import ExpenseTrackerDatabase and ExpenseDao
// from their actual packages in your project.

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): ExpenseTrackerDatabase {
        return ExpenseTrackerDatabase.getDatabase(application)
    }

    @Provides
    fun provideExpenseDao(
        database: ExpenseTrackerDatabase
    ): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideCategoryDao(
        database: ExpenseTrackerDatabase
    ): CategoriesDao {
        return database.categoriesDao()
    }

    @Provides
    fun provideMonthlySummaryDao(
        database: ExpenseTrackerDatabase
    ): MonthlySummaryDao {
        return database.monthlySummaryDao()
    }

    @Provides
    fun provideCacheCrudDao(database: ExpenseTrackerDatabase): CacheCrudDao {
    return database.cacheCrudDao()
    }

    @Provides
    fun provideCategoryCacheDao(database: ExpenseTrackerDatabase): CategoryCacheCrudDao {
        return database.categoryCacheCrudDao()
    }

    @Provides
    fun provideSummaryCacheDao(database: ExpenseTrackerDatabase): SummaryCacheCrudDao {
        return database.summaryCacheCrudDao()
    }

}