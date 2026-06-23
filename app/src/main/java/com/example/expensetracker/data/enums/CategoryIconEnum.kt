package com.example.expensetracker.data.enums

import androidx.annotation.DrawableRes
import com.example.expensetracker.R

enum class CategoryIconEnum(val key: String, @DrawableRes val resId: Int)  {
    FOOD("food", R.drawable.food_dinner),
    TRANSPORT("transport", R.drawable.transport),
    ENTAIRTAINEMT("entertainment",R.drawable.entertainment),
    SHOPPING("shopping", R.drawable.shopping_cart),
    BILLS("bills", R.drawable.bills),
    HEALTH("health", R.drawable.health),
    OTHER("other",R.drawable.info)
}