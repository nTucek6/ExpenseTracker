package com.example.expensetracker.data.repository.impl

import android.util.Log
import androidx.lifecycle.asFlow
import com.example.expensetracker.data.dao.CacheCrudDao
import com.example.expensetracker.data.dao.CategoriesDao
import com.example.expensetracker.data.dao.CategoryCacheCrudDao
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.dao.MonthlySummaryDao
import com.example.expensetracker.data.dao.SummaryCacheCrudDao
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.data.repository.SyncRepository
import com.example.expensetracker.data.viewModel.CategoriesViewModel
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.database.mappers.toFirebaseCategories
import com.example.expensetracker.firebase.database.mappers.toFirebaseExpenses
import com.example.expensetracker.firebase.database.mappers.toFirebaseMonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseUsers
import com.google.firebase.auth.FirebaseUser
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.math.exp

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val firebaseDb: FirebaseDb,

    private val summaryCacheDao : SummaryCacheCrudDao,
    private val cacheDao : CacheCrudDao,
    private val categoryCacheDao : CategoryCacheCrudDao,

    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoriesDao,
    private val summaryDao: MonthlySummaryDao
) : SyncRepository {

    override suspend fun syncRecentUpdates(user: FirebaseUser){
        val cache = cacheDao.getAllCrud().first()
        val summaryCache = summaryCacheDao.getAllSummaryCrud().first()
        val categoryCache = categoryCacheDao.getAllCrud().first()

        for (c in categoryCache) {
            if (c.action == CrudActionEnum.INSERT || c.action == CrudActionEnum.UPDATE) {
                val category = categoryDao.findById(c.categoryId)
                firebaseDb.updateOrCreateCategoryTransaction(user.uid, category)
            } else if (c.action == CrudActionEnum.DELETE) {
                firebaseDb.deleteCategory(user.uid, c.categoryId)
            }
        }

        for (c in cache) {
            if (c.action == CrudActionEnum.INSERT || c.action == CrudActionEnum.UPDATE) {
                val expense = expenseDao.findById(c.expenseId)
                firebaseDb.updateOrCreateExpenseTransaction(user.uid, expense)
            } else if (c.action == CrudActionEnum.DELETE) {
                firebaseDb.deleteExpense(user.uid, c.expenseId)
            }
        }
        for (c in summaryCache) {
            if (c.action == CrudActionEnum.UPDATE) {
                val monthlySummary = summaryDao.findById(c.year, c.month)
                firebaseDb.updateSummaryTransaction(user.uid, monthlySummary)
            }
        }

        summaryCacheDao.deleteAll()
        cacheDao.deleteAll()
        categoryCacheDao.deleteAll()
    }


   override suspend fun syncData(
        user: FirebaseUser
    ) {
        val expenseList = expenseDao.getAllExpenses()
            .asFlow()
            .filter { it.isNotEmpty() }
            .first()
        val summaryList = summaryDao.getAllMonthBudget()
            .asFlow()
            .filter { it.isNotEmpty() }
            .first()

        val categoriesList =
            categoryDao.getAllCategories().asFlow().filter { it.isNotEmpty() }.first()

        val expense = expenseList.toFirebaseExpenses()
        val summary = summaryList.toFirebaseMonthlySummary()
        val categories = categoriesList.toFirebaseCategories()

        Log.d("FirebaseCheckData", expenseList.toString())

        val expenseMap = expense.associate { it.id to it }
        val summaryMap = summary.associate { "${it.year}-${it.month}" to it }
        val categoryMap = categories.associate { it.id to it }
        val userData = FirebaseUsers(user.uid, user.email, expenseMap, summaryMap, categoryMap)

        firebaseDb.getUserRef().child(user.uid).setValue(userData)
            .addOnSuccessListener { Log.d("FirebaseCheck", "User data saved") }
            .addOnFailureListener { e -> Log.e("FirebaseCheck", "Save failed", e) }

    }


}