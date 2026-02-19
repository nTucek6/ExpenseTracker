package com.example.expensetracker.firebase.database

import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import com.example.expensetracker.data.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.enums.CrudActionEnum
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.mappers.toFirebaseExpenses
import com.example.expensetracker.firebase.database.mappers.toFirebaseMonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseExpense
import com.example.expensetracker.firebase.database.model.FirebaseMonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseUsers
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlin.getValue


object FirebaseDb {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    suspend fun syncData(
        user: FirebaseUser,
        expenseViewModel: ExpenseViewModel,
        summaryViewModel: MonthlySummaryViewModel
    ) {
         val expenseList = expenseViewModel.allExpenses
              .asFlow()
              .filter { it.isNotEmpty() }
              .first()
          val summaryList = summaryViewModel.getAllMonthBudget
              .asFlow()
              .filter { it.isNotEmpty() }
              .first()


          val expense = expenseList.toFirebaseExpenses()
          val summary = summaryList.toFirebaseMonthlySummary()

          val expenseMap = expense.associate { "${it.id}" to it }
          val userData = FirebaseUsers(user.uid, user.email, expenseMap, summary)
          usersRef.child(user.uid).setValue(userData)
              .addOnSuccessListener { Log.d("FirebaseCheck", "User data saved") }
              .addOnFailureListener { e -> Log.e("FirebaseCheck", "Save failed", e) }

    }

    suspend fun syncRecentUpdates(
        user: FirebaseUser,
        expenseViewModel: ExpenseViewModel
    ) {
        val cache = expenseViewModel.allCachesCrud.first()

        for (c in cache) {
            if (c.action == CrudActionEnum.INSERT || c.action == CrudActionEnum.UPDATE) {
                val expense = expenseViewModel.getExpenseById(c.expenseId)
                updateOrCreateExpense(user.uid, expense)

            } else if (c.action == CrudActionEnum.DELETE) {
                deleteExpense(user.uid, c.expenseId)
            }
        }
        expenseViewModel.deleteFromCacheCrud()
    }


    suspend fun checkUserExist(uid: String): Boolean {
        val userUidRef = usersRef.child(uid)
        return try {
            val snapshot = userUidRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Check failed", e)
            false
        }
    }

    suspend fun getUserExpensesOnce(uid: String): List<Expense> {
        val expensesRef = database.getReference("users").child(uid).child("expenses")
        val snapshot = expensesRef.get().await()
        return snapshot.children.mapNotNull {
            it.getValue(FirebaseExpense::class.java)?.toExpense()
        }
    }

    suspend fun getUserSummaryOnce(uid: String): List<MonthlySummary> {
        val summaryRef = database.getReference("users").child(uid).child("summary")
        val snapshot = summaryRef.get().await()
        return snapshot.children.mapNotNull {
            it.getValue(FirebaseMonthlySummary::class.java)?.toMonthlySummary()
        }
    }

    fun updateOrCreateExpense(userUid: String?, expense: Expense) {
        val key = expense.id.toString()
        usersRef.child("$userUid/expenses/$key").setValue(expense)
            .addOnSuccessListener {
                Log.d("FirebaseData", "created /expenses/$key")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseData", "Create/Update failed", e)
            }
    }

    fun deleteExpense(userUid: String, expenseId: Int) {
        val key = expenseId.toString()
        usersRef.child("$userUid/expenses/$key").removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Deleted /expenses/$key")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Delete failed", e)
            }
    }

}