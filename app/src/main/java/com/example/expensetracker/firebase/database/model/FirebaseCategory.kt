package com.example.expensetracker.firebase.database.model

import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.enums.CategoryIconEnum

data class FirebaseCategory(
    val id: String = "",
    val displayName: String,
    val image: CategoryIconEnum,
    val isDefault: Boolean
) {
    fun toCategories(): Categories = Categories(
        id = id.toIntOrNull() ?: 0,
        displayName = displayName,
        image = image,
        isDefault = isDefault
    )
}