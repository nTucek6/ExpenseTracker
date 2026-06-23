package com.example.expensetracker.data.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.CacheCrud
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.NetworkViewModel
import com.example.expensetracker.utils.SharedPreferencesUtils
import com.example.expensetracker.utils.ViewModelUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseTrackerDatabase.getDatabase(application).expenseDao()

    private val cacheDao = ExpenseTrackerDatabase.getDatabase(application).cacheCrudDao()

    val context = getApplication<Application>()
    val googleAuthClient = GoogleAuthClient(context.applicationContext)

    val networkViewModel = NetworkViewModel(context)

    val allExpenses = expenseDao.getAllExpenses()
    val totalSpent = expenseDao.getTotalSpent()

    //val recentExpenses = expenseDao.getRecentExpenses()

    val recentExpensesWithCategory = expenseDao.getRecentExpensesWithCategory()

    val allCachesCrud = cacheDao.getAllCrud()

    suspend fun getDailyBudgetSpent(dateFrom: Long?, dateTo:Long?) = expenseDao.getDailyBudgetSpent(dateFrom,dateTo)

    suspend fun getWeeklyBudgetSpent(dateFrom: Long?, dateTo:Long?) = expenseDao.getWeeklyBudgetSpent(dateFrom,dateTo)

    suspend fun getMonthlyBudgetSpent(dateFrom: Long?, dateTo:Long?) = expenseDao.getMonthlyBudgetSpent(dateFrom,dateTo)

    suspend fun getSpentPerCategory(dateFrom: Long?, dateTo:Long?) = expenseDao.getSpentPerCategory(dateFrom,dateTo)


    suspend fun getExpenseById(id: Int): Expense = expenseDao.findById(id)

    suspend fun getExpenseByRemoteId(id: String): Expense = expenseDao.findByRemoteId(id)

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    fun syncFirebaseToRoom() {
        viewModelScope.launch {
            try {
                val firebaseExpenses = FirebaseDb.getUserExpensesOnce(userId)
                expenseDao.insertAll(firebaseExpenses)
                Log.d("Sync", "Firebase → Room: ${firebaseExpenses.size} expenses")
            } catch (e: Exception) {
                Log.e("Sync", "Failed", e)
            }
        }
    }

    private val _query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesPaging: Flow<PagingData<ExpenseWithGroupSum>> = _query
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 15,
                    prefetchDistance = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { expenseDao.getExpensesPaging(query) }
            ).flow.cachedIn(viewModelScope)
        }
    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun insert(amount: Double, description: String?, category: Int, createdAt: Long) {
        val expense = Expense(
            amount = amount,
            description = description?.trim(),
            categoryId = category,
            createdAt = createdAt,
            remoteId = UUID.randomUUID().toString()
        )
        viewModelScope.launch {
            withContext(NonCancellable) {
                val id = expenseDao.insert(expense)

                val newExpense = expense.copy(id = id.toInt())

                val online = networkViewModel.isOnline.first()

                if (online) {
                    firebaseSync(newExpense.copy(id = id.toInt()))
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    cacheDao.insert(
                        CacheCrud(
                            expenseId = newExpense.remoteId,
                            action = CrudActionEnum.INSERT
                        )
                    )
                }
            }
        }
    }

    fun update(
        id: Int,
        amount: Double,
        description: String?,
        categoryId: Int,
        createdAt: Long,
        remoteId: String
    ) {
        val updatedExpense = Expense(
            id = id,
            amount = amount,
            description = description?.trim(),
            categoryId = categoryId,
            createdAt = createdAt,
            remoteId = remoteId
        )
        viewModelScope.launch {
            withContext(NonCancellable) {
                try {
                    expenseDao.update(updatedExpense)
                    val online = networkViewModel.isOnline.first()
                    if (online) {
                        firebaseSync(updatedExpense)
                    } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                        cacheDao.insert(
                            CacheCrud(
                                expenseId = updatedExpense.remoteId,
                                action = CrudActionEnum.UPDATE
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e("ExpenseUpdate", "Update failed", e)
                }
            }
        }
    }
    fun delete(expense: Expense) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                expenseDao.delete(expense)

                val online = networkViewModel.isOnline.first()
                if (online) {
                    deleteExpense(expense.remoteId)
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    cacheDao.insert(
                        CacheCrud(
                            expenseId = expense.remoteId,
                            action = CrudActionEnum.DELETE
                        )
                    )
                }
            }
        }
    }
    fun deleteFromCacheCrud() {
        viewModelScope.launch {
            cacheDao.deleteAll()
        }
    }
    private fun firebaseSync(updatedExpense: Expense) {
        val isSignedIn = googleAuthClient.isSignedIn.value
        val userUid = googleAuthClient.getUser()?.uid
        val isSyncOn: Boolean =
            SharedPreferencesUtils.getAutoSync(context.applicationContext)
        if (isSyncOn && isSignedIn && userUid != null) {
            FirebaseDb.updateOrCreateExpense(userUid, updatedExpense)
        }
    }
    private fun deleteExpense(expenseId: String) {
        val isSignedIn = googleAuthClient.isSignedIn.value
        val userUid = googleAuthClient.getUser()?.uid
        val isSyncOn: Boolean =
            SharedPreferencesUtils.getAutoSync(context.applicationContext)
        if (isSyncOn && isSignedIn && userUid != null) {
            FirebaseDb.deleteExpense(userUid, expenseId)
        }
    }
}