package com.example.expensetracker.data.viewModel

import android.app.Application
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
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.ConnectivityObserver
import com.example.expensetracker.utils.SharedPreferencesUtils
import com.example.expensetracker.utils.ViewModelUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ExpenseViewModel(application: Application,
                       private val expenseRepository: ExpenseRepository) : AndroidViewModel(application) {
    //private val expenseDao = ExpenseTrackerDatabase.getDatabase(application).expenseDao()

    private val cacheDao = ExpenseTrackerDatabase.getDatabase(application).cacheCrudDao()

    val context = getApplication<Application>()
    val googleAuthClient = GoogleAuthClient(context.applicationContext)

    val connectivityObserver = ConnectivityObserver(context)

    val isOnline: StateFlow<Boolean> =
        connectivityObserver.isOnline
            .debounce(500L)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    //val allExpenses = expenseDao.getAllExpenses()
    val allExpenses = expenseRepository.getAllExpenses()
    //val totalSpent = expenseDao.getTotalSpent()
    val totalSpent = expenseRepository.getTotalSpent()

    //val recentExpenses = expenseDao.getRecentExpenses()

    //val recentExpensesWithCategory = expenseDao.getRecentExpensesWithCategory()
    val recentExpensesWithCategory = expenseRepository.getRecentExpensesWithCategory()
    val allCachesCrud = cacheDao.getAllCrud()

    suspend fun getDailyBudgetSpent(dateFrom: Long?, dateTo: Long?) =
        expenseRepository.getDailyBudgetSpent(dateFrom, dateTo)

    suspend fun getWeeklyBudgetSpent(dateFrom: Long?, dateTo: Long?) =
        expenseRepository.getWeeklyBudgetSpent(dateFrom, dateTo)

    suspend fun getMonthlyBudgetSpent(dateFrom: Long?, dateTo: Long?) =
        expenseRepository.getMonthlyBudgetSpent(dateFrom, dateTo)

    suspend fun getSpentPerCategory(dateFrom: Long?, dateTo: Long?) =
        expenseRepository.getSpentPerCategory(dateFrom, dateTo)


    suspend fun getExpenseById(id: String): Expense = expenseRepository.findById(id)

    //private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    suspend fun syncFirebaseToRoom() {
        //viewModelScope.launch {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            val firebaseExpenses = FirebaseDb.getUserExpensesOnce(userId)
            expenseRepository.replaceAll(firebaseExpenses)
            // expenseDao.insertAll(firebaseExpenses)
            Log.d("Sync", "Firebase → Room: ${firebaseExpenses.size} expenses")
        } catch (e: Exception) {
            Log.e("Sync", "Failed", e)
        }
        //   }
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
                pagingSourceFactory = { expenseRepository.getExpensesPaging(query) }
            ).flow.cachedIn(viewModelScope)
        }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun insert(amount: Double, description: String?, category: String, createdAt: Long) {
        val expense = Expense(
            id = UUID.randomUUID().toString(),
            amount = amount,
            description = description?.trim(),
            categoryId = category,
            createdAt = createdAt,
        )
        viewModelScope.launch {
            withContext(NonCancellable) {
                expenseRepository.insert(expense)
                val online = connectivityObserver.isOnline.first()
                if (online) {
                    firebaseSync(expense)
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    cacheDao.insert(
                        CacheCrud(
                            expenseId = expense.id,
                            action = CrudActionEnum.INSERT
                        )
                    )
                }
            }
        }
    }

    fun update(
        id: String,
        amount: Double,
        description: String?,
        categoryId: String,
        createdAt: Long,
    ) {
        val updatedExpense = Expense(
            id = id,
            amount = amount,
            description = description?.trim(),
            categoryId = categoryId,
            createdAt = createdAt,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            withContext(NonCancellable) {
                try {
                    expenseRepository.update(updatedExpense)
                    val online = connectivityObserver.isOnline.first()
                    if (online) {
                        firebaseSync(updatedExpense)
                    } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                        cacheDao.insert(
                            CacheCrud(
                                expenseId = updatedExpense.id,
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
                expenseRepository.delete(expense)

                val online = connectivityObserver.isOnline.first()
                if (online) {
                    deleteExpense(expense.id)
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    cacheDao.insert(
                        CacheCrud(
                            expenseId = expense.id,
                            action = CrudActionEnum.DELETE
                        )
                    )
                }
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            expenseRepository.deleteAll()
        }
    }


    fun deleteFromCacheCrud() {
        viewModelScope.launch {
            cacheDao.deleteAll()
        }
    }

    private suspend fun firebaseSync(updatedExpense: Expense) {
        val isSignedIn = googleAuthClient.isSignedIn.value
        val userUid = googleAuthClient.getUser()?.uid
        val isSyncOn: Boolean =
            SharedPreferencesUtils.getAutoSync(context.applicationContext)
        if (isSyncOn && isSignedIn && userUid != null) {
            /*val updateFlag = FirebaseDb.checkExpenseConflictData(userUid, updatedExpense.id, updatedExpense.updatedAt)
            Log.d("Compare data", updateFlag.toString())
            if (updateFlag) {
                FirebaseDb.updateOrCreateExpense(userUid, updatedExpense)
            } else {
                Toast.makeText(context,"There is newer data on remote server...", Toast.LENGTH_SHORT).show()
            }*/
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