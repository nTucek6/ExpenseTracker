package com.example.expensetracker.data.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.expensetracker.R

enum class CategoryIconEnum(
    val key: String,
    @DrawableRes val resId: Int,
    @StringRes val displayName: Int
) {
    FOOD("food", R.drawable.food_dinner, R.string.FOOD),
    TRANSPORT("transport", R.drawable.transport, R.string.TRANSPORT),
    ENTAIRTAINEMT("entertainment", R.drawable.entertainment, R.string.ENTERTAINMENT),
    SHOPPING("shopping", R.drawable.shopping_cart, R.string.SHOPPING),
    BILLS("bills", R.drawable.bills, R.string.BILLS),
    HEALTH("health", R.drawable.health, R.string.HEALTH),
    OTHER("other", R.drawable.info, R.string.OTHER)
}