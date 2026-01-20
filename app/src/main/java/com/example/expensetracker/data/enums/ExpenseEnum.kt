package com.example.expensetracker.data.enums

import android.content.Context
import com.example.expensetracker.R

enum class ExpenseEnum(val displayName: String, val imageSvg: Int) {
    FOOD("Food", R.drawable.food_dinner),
    TRANSPORT("Transport", R.drawable.transport),
    ENTERTAINMENT("Entertainment", R.drawable.entertainment),
    SHOPPING("Shopping", R.drawable.shopping_cart),
    BILLS("Bills", R.drawable.bills),
    HEALTH("Health", R.drawable.health),
    OTHER("Other", R.drawable.info);

}