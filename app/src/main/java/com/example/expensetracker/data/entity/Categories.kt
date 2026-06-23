package com.example.expensetracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.enums.CategoryIconEnum
import java.util.UUID

@Entity(tableName = "categories")
data class Categories(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val displayName: String,
    val image: CategoryIconEnum,
    val isDefault: Boolean,
    val updatedAt: Long = System.currentTimeMillis(),
)
