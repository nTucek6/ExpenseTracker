package com.example.expensetracker.data.model

import androidx.room.DatabaseView

@DatabaseView("""
    SELECT COALESCE(SUM(e.amount), 0) as amount,
    e.createdAt as date
    FROM expenses e
    GROUP BY date(datetime(e.createdAt / 1000, 'unixepoch'))
""")
data class DailyBudgetSpent(
    val amount: Double,
    val date: Long,
)
