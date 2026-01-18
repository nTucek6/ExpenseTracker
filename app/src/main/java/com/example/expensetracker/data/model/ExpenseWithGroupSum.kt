package com.example.expensetracker.data.model


import android.os.Parcelable
import androidx.room.DatabaseView
import com.example.expensetracker.data.enums.ExpenseEnum
import kotlinx.parcelize.Parcelize

/*@DatabaseView("""
      SELECT *, 
           SUM(amount) OVER (
               PARTITION BY date(createdAt / 1000, 'unixepoch')
               ORDER BY createdAt
           ) AS dailySum
    FROM expenses 
    ORDER BY createdAt DESC
""")*/
@DatabaseView("""
    SELECT * FROM expenses ORDER BY createdAt DESC
""")
data class ExpenseWithGroupSum(
    val id: Int = 0,
    val amount : Double,
    val category : ExpenseEnum,
    val description: String? = null,
    val createdAt : Long = System.currentTimeMillis(),
    val dailySum: Double
)
