package com.example.expensetracker.firebase.database.model

import com.example.expensetracker.data.entity.MonthlySummary
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class FirebaseMonthlySummary(
    val year: Int = 0,
    val month: Int = 0,
    val money: Double = 0.0,
) {
    constructor() : this(0, 0, 0.0)

    fun toMonthlySummary(): MonthlySummary = MonthlySummary(
        year = year,
        month = month,
        money = money
    )
}