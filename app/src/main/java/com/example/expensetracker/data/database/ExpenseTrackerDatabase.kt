package com.example.expensetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expensetracker.data.dao.CacheCrudDao
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.dao.MonthlySummaryDao
import com.example.expensetracker.data.entity.CacheCrud
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.model.BudgetWithSpent
import com.example.expensetracker.data.model.ExpenseWithGroupSum

@Database(entities = [Expense::class, MonthlySummary::class, CacheCrud::class], [BudgetWithSpent::class, ExpenseWithGroupSum::class] ,version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun monthlySummaryDao(): MonthlySummaryDao

    abstract fun cacheCrudDao(): CacheCrudDao
    companion object {
        @Volatile
        private var INSTANCE: ExpenseTrackerDatabase? = null
        fun getDatabase(context: Context): ExpenseTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseTrackerDatabase::class.java,
                    "expense_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}