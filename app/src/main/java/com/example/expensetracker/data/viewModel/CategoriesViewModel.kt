package com.example.expensetracker.data.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.expensetracker.data.dao.CategoryCacheCrudDao
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.CacheCrud
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.entity.CategoryCacheCrud
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.CategoryIconEnum
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.data.model.ManageCategories
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.database.FirebaseDb.checkCategoryConflictData
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.NetworkViewModel
import com.example.expensetracker.utils.SharedPreferencesUtils
import com.example.expensetracker.utils.ViewModelUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    val context = getApplication<Application>()

    val googleAuthClient = GoogleAuthClient(context.applicationContext)

    val networkViewModel = NetworkViewModel(context)

    private val categoryCacheDao =
        ExpenseTrackerDatabase.getDatabase(application).categoryCacheCrudDao()

    private val categoriesDao = ExpenseTrackerDatabase.getDatabase(application).categoriesDao()

    val allCategoryCachesCrud = categoryCacheDao.getAllCrud()

    suspend fun findById(id: String): Categories = categoriesDao.findById(id)

    val allCategories = categoriesDao.getAllCategories()

    //private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    suspend fun getCategoryById(id: String): Categories = categoriesDao.findById(id)

    fun insert(displayName: String, imageSvg: CategoryIconEnum) {
        val category = Categories(
            id = UUID.randomUUID().toString(),
            displayName = displayName,
            image = imageSvg,
            isDefault = false,
        )

        viewModelScope.launch {
            withContext(NonCancellable) {
                categoriesDao.insert(category)

                val newCategory = category.copy(id = category.id)

                val online = networkViewModel.isOnline.first()

                if (online) {
                    firebaseSync(category.copy(id = category.id))
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    categoryCacheDao.insert(
                        CategoryCacheCrud(
                            categoryId = newCategory.id,
                            action = CrudActionEnum.INSERT
                        )
                    )
                }
            }
        }

    }

    fun update(category: ManageCategories) {
        val updatedCategory = Categories(
            id = category.id,
            displayName = category.displayName,
            image = category.image,
            isDefault = category.isDefault,
            updatedAt = category.updatedAt
        )

        viewModelScope.launch {
            withContext(NonCancellable) {
                try {
                    categoriesDao.update(updatedCategory)
                    val online = networkViewModel.isOnline.first()
                    if (online) {
                        firebaseSync(updatedCategory)
                    } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                        categoryCacheDao.insert(
                            CategoryCacheCrud(
                                categoryId = updatedCategory.id,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryPaging: Flow<PagingData<ManageCategories>> =
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

                val online = networkViewModel.isOnline.first()
                if (online) {
                    deleteCategory(category.id)
                } else if (ViewModelUtils.checkOfflineSync(googleAuthClient, context)) {
                    categoryCacheDao.insert(
                        CategoryCacheCrud(
                            categoryId = category.id,
                            action = CrudActionEnum.DELETE
                        )
                    )
                }
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            categoriesDao.deleteAll()
        }
    }

    fun deleteFromCategoryCacheCrud() {
        viewModelScope.launch {
            categoryCacheDao.deleteAll()
        }
    }

    private suspend fun firebaseSync(updatedCategory: Categories) {
        val isSignedIn = googleAuthClient.isSignedIn.value
        val userUid = googleAuthClient.getUser()?.uid
        val isSyncOn: Boolean =
            SharedPreferencesUtils.getAutoSync(context.applicationContext)
        if (isSyncOn && isSignedIn && userUid != null) {
            val updatedFlag = checkCategoryConflictData(userUid, updatedCategory.id, updatedCategory.updatedAt)
            if (updatedFlag) {
                FirebaseDb.updateOrCreateCategory(userUid, updatedCategory)
            } else {
                Toast.makeText(context,"There is newer data on remote server...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteCategory(categoryId: String) {
        val isSignedIn = googleAuthClient.isSignedIn.value
        val userUid = googleAuthClient.getUser()?.uid
        val isSyncOn: Boolean =
            SharedPreferencesUtils.getAutoSync(context.applicationContext)
        if (isSyncOn && isSignedIn && userUid != null) {
            FirebaseDb.deleteCategory(userUid, categoryId)
        }
    }

    suspend fun syncFirebaseToRoom() {
        //viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                val firebaseCategories = FirebaseDb.getUserCategoriesOnce(userId)
                categoriesDao.replaceAll(firebaseCategories)
                //categoriesDao.insertAll(firebaseCategories)
                Log.d("Sync", "Firebase → Room: ${firebaseCategories.size} categories")
            } catch (e: Exception) {
                Log.e("Sync", "Failed", e)
            }
     //   }
    }

}