package com.example.expensetracker.data.model

import androidx.room.DatabaseView

@DatabaseView("""
    SELECT 
        b.year,
        b.month,
        b.money,
        COALESCE(SUM(e.amount), 0) as spent
    FROM monthly_summary b
    LEFT JOIN expenses e ON 
        b.year = CAST(strftime('%Y', datetime(e.createdAt/1000, 'unixepoch')) AS INT)
        AND b.month = CAST(strftime('%m', datetime(e.createdAt/1000, 'unixepoch')) AS INT)
    GROUP BY b.year, b.month
""")
data class BudgetWithSpent(
    val year: Int,
    val month: Int,
    val money: Double,
    val spent: Double
)
