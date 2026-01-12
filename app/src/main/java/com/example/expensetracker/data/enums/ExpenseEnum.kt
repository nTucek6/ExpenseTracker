package com.example.expensetracker.data.enums

enum class ExpenseEnum {
    FOOD,           // Groceries, restaurants
    TRANSPORT,      // Gas, public transport
    ENTERTAINMENT,  // Movies, games
    SHOPPING,       // Clothes, electronics
    BILLS,          // Utilities, rent
    HEALTH,         // Gym, medicine
    OTHER;
    fun getDisplayName(): String = when (this) {
        FOOD -> "Food"
        TRANSPORT -> "Transport"
        ENTERTAINMENT -> "Entertainment"
        SHOPPING -> "Shopping"
        BILLS -> "Bills"
        HEALTH -> "Health"
        OTHER -> "Other"
    }
}