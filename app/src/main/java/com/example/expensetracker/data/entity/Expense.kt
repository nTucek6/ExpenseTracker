package com.example.expensetracker.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "expenses")
data class Expense (
    //@PrimaryKey(autoGenerate = true)
   // val id: Int = 0,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val amount : Double,
    val categoryId : String,
    val description: String? = null,
    val createdAt : Long,
    val updatedAt: Long = System.currentTimeMillis(),
) : Parcelable