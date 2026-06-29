package com.example.expensetracker.firebase.database

import android.util.Log
import androidx.lifecycle.asFlow
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.enums.CategoryIconEnum
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.data.viewModel.CategoriesViewModel
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.mappers.toFirebaseCategories
import com.example.expensetracker.firebase.database.mappers.toFirebaseExpenses
import com.example.expensetracker.firebase.database.mappers.toFirebaseMonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseCategory
import com.example.expensetracker.firebase.database.model.FirebaseExpense
import com.example.expensetracker.firebase.database.model.FirebaseMonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseUsers
import com.example.expensetracker.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlin.collections.get

object FirebaseDb {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    suspend fun syncData(
        user: FirebaseUser,
        expenseViewModel: ExpenseViewModel,
        summaryViewModel: MonthlySummaryViewModel,
        categoriesViewModel: CategoriesViewModel
    ) {
        val expenseList = expenseViewModel.allExpenses
            .asFlow()
            .filter { it.isNotEmpty() }
            .first()
        val summaryList = summaryViewModel.getAllMonthBudget
            .asFlow()
            .filter { it.isNotEmpty() }
            .first()

        val categoriesList =
            categoriesViewModel.allCategories.asFlow().filter { it.isNotEmpty() }.first()

        val expense = expenseList.toFirebaseExpenses()
        val summary = summaryList.toFirebaseMonthlySummary()
        val categories = categoriesList.toFirebaseCategories()

        Log.d("FirebaseCheckData", expenseList.toString())

        val expenseMap = expense.associate { it.id to it }
        val summaryMap = summary.associate { "${it.year}-${it.month}" to it }
        val categoryMap = categories.associate { it.id to it }
        val userData = FirebaseUsers(user.uid, user.email, expenseMap, summaryMap, categoryMap)

        usersRef.child(user.uid).setValue(userData)
            .addOnSuccessListener { Log.d("FirebaseCheck", "User data saved") }
            .addOnFailureListener { e -> Log.e("FirebaseCheck", "Save failed", e) }

    }

    suspend fun syncRecentUpdates(
        user: FirebaseUser,
        expenseViewModel: ExpenseViewModel,
        summaryViewModel: MonthlySummaryViewModel,
        categoriesViewModel: CategoriesViewModel
    ) {
        val cache = expenseViewModel.allCachesCrud.first()
        val summaryCache = summaryViewModel.allCachesCrud.first()
        val categoryCache = categoriesViewModel.allCategoryCachesCrud.first()

        for (c in categoryCache) {
            if (c.action == CrudActionEnum.INSERT || c.action == CrudActionEnum.UPDATE) {
                val category = categoriesViewModel.getCategoryById(c.categoryId)

                val updatedFlag =
                    checkCategoryConflictData(user.uid, category.id, category.updatedAt)
                if (updatedFlag) {
                    updateOrCreateCategory(user.uid, category)
                }
            } else if (c.action == CrudActionEnum.DELETE) {
                deleteCategory(user.uid, c.categoryId)
            }
        }

        for (c in cache) {
            if (c.action == CrudActionEnum.INSERT || c.action == CrudActionEnum.UPDATE) {
                val expense = expenseViewModel.getExpenseById(c.expenseId)

                val updateFlag = checkExpenseConflictData(user.uid, expense.id, expense.updatedAt)

                if (updateFlag) {
                    updateOrCreateExpense(user.uid, expense)
                }

            } else if (c.action == CrudActionEnum.DELETE) {
                deleteExpense(user.uid, c.expenseId)
            }
        }
        for (c in summaryCache) {
            if (c.action == CrudActionEnum.UPDATE) {
                val monthlySummary = summaryViewModel.getSummaryById(c.year, c.month)
                updateMonthlyLimit(user.uid, monthlySummary.money, "${c.year}-${c.month}")
            }
        }

        summaryViewModel.deleteFromCacheCrud()
        expenseViewModel.deleteFromCacheCrud()
        categoriesViewModel.deleteFromCategoryCacheCrud()
    }


    suspend fun getUserExpensesOnce(uid: String): List<Expense> {
        val expensesRef = database.getReference("users").child(uid).child("expenses")
        val snapshot = expensesRef.get().await()
        return snapshot.children.mapNotNull { snap ->
            val map = snap.value as? Map<*, *> ?: return@mapNotNull null

            val id = map["id"]?.toString() ?: ""
            val amount = (map["amount"] as? Number)?.toDouble() ?: 0.0
            val category = map["categoryId"]?.toString() ?: ""
            val description = map["description"]?.toString()
            val createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L
            val updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: 0L

            FirebaseExpense(id, amount, category, description, createdAt, updatedAt).toExpense()
        }
    }

    suspend fun getUserSummaryOnce(uid: String): List<MonthlySummary> {
        val summaryRef = database.getReference("users").child(uid).child("summary")
        val snapshot = summaryRef.get().await()
        return snapshot.children.mapNotNull {
            it.getValue(FirebaseMonthlySummary::class.java)?.toMonthlySummary()
        }
    }

    suspend fun getUserCategoriesOnce(uid: String): List<Categories> {
        val summaryRef = database.getReference("users").child(uid).child("categories")
        val snapshot = summaryRef.get().await()
        return snapshot.children.mapNotNull { snap ->
            val map = snap.value as? Map<*, *> ?: return@mapNotNull null

            val id = map["id"]?.toString() ?: ""
            val displayName = map["displayName"].toString()
            val imageKey = map["image"] as? String
            val image = CategoryIconEnum.entries.firstOrNull { it.key == imageKey?.lowercase() }
                ?: CategoryIconEnum.OTHER
            val isDefault = (map["default"] as? Boolean) ?: false
            val updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: 0L

            FirebaseCategory(id, displayName, image, isDefault, updatedAt).toCategories()
        }
    }

    fun updateOrCreateExpense(userUid: String?, expense: Expense) {
        val key = expense.id

        usersRef.child("$userUid/expenses/$key").setValue(expense)
            .addOnSuccessListener {
                Log.d("FirebaseData", "created /expenses/$key")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Create/Update failed", e)
            }
    }


    suspend fun checkExpenseConflictData(userUid: String, key: String, updatedAt: Long): Boolean {
        val snapshot = usersRef.child("$userUid/expenses/$key").get().await()
        val expense = FirebaseUtils.snapshotToExpense(snapshot)

        Log.d("Compare data", (expense.updatedAt < updatedAt).toString())
        return expense.updatedAt < updatedAt
    }

    suspend fun checkCategoryConflictData(userUid: String, key: String, updatedAt: Long): Boolean {
        val snapshot = usersRef.child("$userUid/categories/$key").get().await()
        val category = FirebaseUtils.snapshotToCategory(snapshot)

        Log.d("Compare data", (category.updatedAt < updatedAt).toString())
        return category.updatedAt < updatedAt
    }

    fun deleteExpense(userUid: String, expenseId: String) {

        usersRef.child("$userUid/expenses/$expenseId").removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Deleted /expenses/$expenseId")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Delete failed", e)
            }
    }

    fun updateMonthlyLimit(userUid: String?, limit: Double, key: String) {
        usersRef.child("$userUid/summary/$key/money").setValue(limit)
            .addOnSuccessListener {
                Log.d("FirebaseData", "created /expenses/$key")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Create/Update failed", e)
            }
    }

    fun updateOrCreateCategory(userUid: String?, category: Categories) {
        val key = category.id
        usersRef.child("$userUid/categories/$key").setValue(category)
            .addOnSuccessListener {
                Log.d("FirebaseData", "created /categories/$key")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Create/Update failed", e)
            }
    }

    fun deleteCategory(userUid: String, categoryId: String) {
        usersRef.child("$userUid/categories/$categoryId").removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Deleted /expenses/$categoryId")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Delete failed", e)
            }
    }


}