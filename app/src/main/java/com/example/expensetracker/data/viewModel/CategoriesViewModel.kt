package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.CacheCrud
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.utils.ViewModelUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val categoriesDao = ExpenseTrackerDatabase.getDatabase(application).categoriesDao()

    suspend fun findById(id: Int): Categories = categoriesDao.findById(id)

    val allCategories = categoriesDao.getAllCategories()

    fun insert(displayName: String, imageSvg: Int?) {
        val category = Categories(
            displayName = displayName,
            imageSvg = imageSvg ?: 0,
            isDefault = false
        )
        viewModelScope.launch {
            categoriesDao.insert(category)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryPaging: Flow<PagingData<Categories>> =
        Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { categoriesDao.getCategoriesPaging() }
        ).flow.cachedIn(viewModelScope)



    fun delete(category: Categories) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                categoriesDao.delete(category)

               /* val online = networkViewModel.isOnline.first()
                if (online) {
                    deleteExpense(expense.id)
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    cacheDao.insert(
                        CacheCrud(
                            expenseId = expense.id,
                            action = CrudActionEnum.DELETE
                        )
                    )
                }*/
            }
        }
    }

}