package com.example.expensetracker.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.example.expensetracker.R

enum class ExpenseEnum(@StringRes val displayName: Int, val imageSvg: Int) {
    FOOD(R.string.FOOD, R.drawable.food_dinner),
    TRANSPORT(R.string.TRANSPORT, R.drawable.transport),
    ENTERTAINMENT(R.string.ENTERTAINMENT, R.drawable.entertainment),
    SHOPPING(R.string.SHOPPING, R.drawable.shopping_cart),
    BILLS(R.string.BILLS, R.drawable.bills),
    HEALTH(R.string.HEALTH, R.drawable.health),
    OTHER(R.string.OTHER, R.drawable.info);

}