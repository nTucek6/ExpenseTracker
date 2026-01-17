package com.example.expensetracker.firebase.database

import android.util.Log
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseExpense
import com.example.expensetracker.firebase.database.model.FirebaseMonthlySummary
import com.example.expensetracker.firebase.database.model.FirebaseUsers
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


class FirebaseDb {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    /*fun saveData(user: FirebaseUser, expense: List<Expense>, summary: List<MonthlySummary>) {
        usersRef.setValue(Users(user.uid, user.email, expense, summary))
    }*/

    fun syncData(user: FirebaseUser, expense: List<FirebaseExpense>, summary: List<FirebaseMonthlySummary>) {
            val userData = FirebaseUsers(user.uid, user.email, expense, summary)
            usersRef.child(user.uid).setValue(userData)
                .addOnSuccessListener { Log.d("FirebaseCheck", "User data saved") }
                .addOnFailureListener { e -> Log.e("FirebaseCheck", "Save failed", e) }
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


}