package com.example.expensetracker.ui.models

data class MonthItem(
    val value: Int,
    val name: String,
) {
    override fun toString(): String = name
}
