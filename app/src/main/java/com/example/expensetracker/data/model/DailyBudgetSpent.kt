package com.example.expensetracker.data.model

import androidx.room.DatabaseView

@DatabaseView("""
    SELECT
        ((e.createdAt / 1000) / 86400) * 86400 * 1000 AS date,
        COALESCE(SUM(e.amount), 0) AS amount
    FROM expenses e
    GROUP BY (e.createdAt / 1000) / 86400
    ORDER BY date ASC
""")
data class DailyBudgetSpent(
    val amount: Double,
    val date: Long,
)
