package com.example.expensetracker.data.database

import androidx.room.TypeConverter
import com.example.expensetracker.data.enums.ExpenseEnum

class Converters {
    @TypeConverter
    fun fromCategory(category: ExpenseEnum?): String? = category?.name

    @TypeConverter
    fun toCategory(category: String?): ExpenseEnum? =
        category?.let { ExpenseEnum.valueOf(it) }
}