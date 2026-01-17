package com.example.expensetracker.firebase.database.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class FirebaseMonthlySummary (
    val year: Int = 0,
    val month: Int = 0,
    val money: Double = 0.0,
)