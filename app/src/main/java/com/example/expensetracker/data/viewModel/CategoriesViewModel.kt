package com.example.expensetracker.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Categories
import kotlinx.coroutines.launch

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val categoriesDao = ExpenseTrackerDatabase.getDatabase(application).categoriesDao()

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

}