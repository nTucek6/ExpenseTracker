package com.example.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Categories(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val displayName: String,
    val imageSvg: Int,
    val isDefault: Boolean
)
