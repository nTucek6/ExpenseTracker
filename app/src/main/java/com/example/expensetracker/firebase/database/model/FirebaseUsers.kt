package com.example.expensetracker.firebase.database.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseUsers(
    val uid: String = "",
    val email: String? = null,
    //val expenses: List<FirebaseExpense>? = null,
    val expenses: Map<String, FirebaseExpense>,
    val summary: List<FirebaseMonthlySummary>? = null
)




