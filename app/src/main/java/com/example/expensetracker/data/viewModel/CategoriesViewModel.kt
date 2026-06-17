package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val categoriesDao = ExpenseTrackerDatabase.getDatabase(application).categoriesDao()

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

}