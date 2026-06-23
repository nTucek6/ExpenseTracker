package com.example.expensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.enums.CrudActionEnum

@Entity("category_cache_crud")
data class CategoryCacheCrud (
    @PrimaryKey
    val categoryId: String,
    val action: CrudActionEnum,
)