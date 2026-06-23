package com.example.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.enums.CategoryIconEnum

@Entity(tableName = "categories")
data class Categories(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val displayName: String,
    val image: CategoryIconEnum,
    val isDefault: Boolean
)
