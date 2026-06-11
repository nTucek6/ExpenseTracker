package com.example.expensetracker.data.model

import androidx.room.DatabaseView

//        ((e.createdAt / 1000) / 86400) * 86400 * 1000 AS date,
//GROUP BY (e.createdAt / 1000) / 86400
@DatabaseView("""
    SELECT
    CAST(strftime('%Y-%m-%d', datetime(e.createdAt/1000, 'unixepoch', 'localtime'))AS TEXT) AS date,
        COALESCE(SUM(e.amount), 0) AS amount
    FROM expenses e
    Group By date
    ORDER BY date ASC
""")
data class DailyBudgetSpent(
    val amount: Double,
    val date: Long,
)
