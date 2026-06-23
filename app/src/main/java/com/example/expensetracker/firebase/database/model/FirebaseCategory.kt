package com.example.expensetracker.firebase.database.model

import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.enums.CategoryIconEnum

data class FirebaseCategory(
    val id: String = "",
    val displayName: String,
    val image: CategoryIconEnum,
    val isDefault: Boolean,
    val updatedAt: Long,
) {
    fun toCategories(): Categories = Categories(
        id = id,
        displayName = displayName,
        image = image,
        isDefault = isDefault,
        updatedAt = updatedAt
    )
}