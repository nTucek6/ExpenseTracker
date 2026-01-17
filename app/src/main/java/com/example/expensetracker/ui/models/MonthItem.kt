package com.example.expensetracker.ui.models

import com.example.expensetracker.data.enums.MonthEnum

data class MonthItem(
    val value: Int,
    val name: String,
) {
    override fun toString(): String = name
}
