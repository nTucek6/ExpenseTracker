package com.example.expensetracker.firebase.database.model

import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.enums.CategoryIconEnum
import java.util.UUID

data class FirebaseCategory(
    val id: String = "",
    val displayName: String,
    val image: CategoryIconEnum,
    val isDefault: Boolean,
    val remoteId: String = UUID.randomUUID().toString(),
) {
    fun toCategories(): Categories = Categories(
        id = id.toIntOrNull() ?: 0,
        displayName = displayName,
        image = image,
        isDefault = isDefault,
        remoteId = remoteId
    )
}