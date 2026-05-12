package com.example.expensetracker.data.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LOG_TAG
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.CacheCrud
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.entity.SummaryCacheCrud
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.data.model.BudgetWithSpent
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MonthlySummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val monthlySummaryDao =
        ExpenseTrackerDatabase.getDatabase(application).monthlySummaryDao()
    private val cacheDao = ExpenseTrackerDatabase.getDatabase(application).summaryCacheCrudDao()

    val context = getApplication<Application>()
    val googleAuthClient = GoogleAuthClient(context.applicationContext)
    val networkViewModel = NetworkViewModel(context)

    val getCurrentMonthBudget = monthlySummaryDao.getCurrentMonthBudget()
    val getAllMonthBudget = monthlySummaryDao.getAllMonthBudget()

    val allCachesCrud = cacheDao.getAllSummaryCrud()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    suspend fun findAllExistingYears(): List<Int> = monthlySummaryDao.findAllExistingYears()

    suspend fun getSummaryById(year: Int, month: Int): MonthlySummary = monthlySummaryDao.findById(year,month)

    private val _queryYear = MutableStateFlow(0)
    private val _queryMonth = MutableStateFlow(0)

    fun getBudget(year: Int, month: Int): LiveData<MonthlySummary?> {
        return monthlySummaryDao.getBudget(year, month)
    }

    fun insertSummary(summary: MonthlySummary) {
        viewModelScope.launch {
            monthlySummaryDao.insert(summary)
        }
    }

    fun updateSummary(summary: MonthlySummary) {
        viewModelScope.launch {
            monthlySummaryDao.update(summary)
        }
    }

    fun createDefaultSummary() {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH) + 1

            val data = monthlySummaryDao.budgetExists(year, month)
            if (data == 0) {
                val defaultBudget = MonthlySummary(
                    year = year,
                    month = month,
                    money = 0.0
                )
                monthlySummaryDao.insert(defaultBudget)
            }
        }
    }

    private fun firebaseSync(limit: Double, key: String){
        val isSignedIn = googleAuthClient.isSignedIn.value
        val userUid = googleAuthClient.getUser()?.uid
        val isSyncOn: Boolean =
            SharedPreferencesUtils.getAutoSync(context.applicationContext)
        if (isSyncOn && isSignedIn && userUid != null) {
            FirebaseDb.updateMonthlyLimit(userUid, limit, key)
        }
    }

    fun updateLatestMonth(limit: Double) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                val now = Calendar.getInstance()
                val year = now.get(Calendar.YEAR)
                val month = now.get(Calendar.MONTH) + 1

                monthlySummaryDao.update(MonthlySummary(year = year, month = month, money = limit))

                val online = networkViewModel.isOnline.first()
                if (online) {
                        firebaseSync(limit, "${year}-${month}")
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient,context)) {
                    cacheDao.insert(
                        SummaryCacheCrud(
                            year = year,
                            month = month,
                            action = CrudActionEnum.UPDATE
                        )
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val summaryPaging: Flow<PagingData<BudgetWithSpent>> =
        combine(_queryYear, _queryMonth) { year, month ->
            year to month
        }.flatMapLatest { (year, month) ->
            Pager(
                config = PagingConfig(
                    pageSize = 15,
                    prefetchDistance = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    monthlySummaryDao.getMonthBudgetPaging(
                        year,
                        month
                    )
                }).flow.cachedIn(viewModelScope)
        }

    fun updateYearQuery(newQuery: Int) {
        _queryYear.value = newQuery
    }

    fun updateMonthQuery(newQuery: Int) {
        _queryMonth.value = newQuery
    }

    fun syncFirebaseToRoom() {
        viewModelScope.launch {
            try {
                val firebaseExpenses = FirebaseDb.getUserSummaryOnce(userId)
                monthlySummaryDao.insertAll(firebaseExpenses)
                Log.d("Sync", "Firebase → Room: ${firebaseExpenses.size} expenses")
            } catch (e: Exception) {
                Log.e("Sync", "Failed", e)
            }
        }
    }

    fun deleteFromCacheCrud() {
        viewModelScope.launch {
            cacheDao.deleteAll()
        }
    }

}