package com.example.expensetracker.ui.models

data class DropdownItem(
    val value: String,
    val name: String,
) {
    override fun toString(): String = name
}
