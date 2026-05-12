package com.example.expensetracker.data.database

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.R
import com.example.expensetracker.data.dao.CacheCrudDao
import com.example.expensetracker.data.dao.CategoriesDao
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.dao.MonthlySummaryDao
import com.example.expensetracker.data.dao.SummaryCacheCrudDao
import com.example.expensetracker.data.entity.CacheCrud
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.entity.SummaryCacheCrud
import com.example.expensetracker.data.enums.ExpenseEnum
import com.example.expensetracker.data.model.BudgetWithSpent
import com.example.expensetracker.data.model.ExpenseWithCategory
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Expense::class, MonthlySummary::class, CacheCrud::class, SummaryCacheCrud::class, Categories::class],
    [BudgetWithSpent::class, ExpenseWithGroupSum::class, ExpenseWithCategory::class], version = 3, exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun monthlySummaryDao(): MonthlySummaryDao

    abstract fun cacheCrudDao(): CacheCrudDao

    abstract fun categoriesDao(): CategoriesDao

    abstract fun summaryCacheCrudDao(): SummaryCacheCrudDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseTrackerDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `categories` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `displayName` TEXT NOT NULL,
                `imageSvg` INTEGER NOT NULL,
                `isDefault` INTEGER NOT NULL
            )
        """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): ExpenseTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseTrackerDatabase::class.java,
                    "expense_tracker_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            setDefaultCategories(context)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun setDefaultCategories(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = INSTANCE?.categoriesDao() ?: return@launch
                ExpenseEnum.entries.forEach { c ->
                   // Log.d("FirstTimeInsert", "${c.displayName}")
                    dao.insert(
                        Categories(
                            displayName = context.getString(c.displayName),
                            imageSvg = c.imageSvg,
                            isDefault = true
                        )
                    )
                }
            }
        }
    }
}