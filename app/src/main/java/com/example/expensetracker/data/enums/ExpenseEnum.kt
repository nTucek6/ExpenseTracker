package com.example.expensetracker.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.example.expensetracker.R

enum class ExpenseEnum(@StringRes val displayName: Int, val image: CategoryIconEnum) {
    FOOD(R.string.FOOD, CategoryIconEnum.FOOD),
    TRANSPORT(R.string.TRANSPORT, CategoryIconEnum.TRANSPORT),
    ENTERTAINMENT(R.string.ENTERTAINMENT, CategoryIconEnum.ENTAIRTAINEMT),
    SHOPPING(R.string.SHOPPING, CategoryIconEnum.SHOPPING),
    BILLS(R.string.BILLS, CategoryIconEnum.BILLS),
    HEALTH(R.string.HEALTH, CategoryIconEnum.HEALTH),
    OTHER(R.string.OTHER, CategoryIconEnum.OTHER);
}