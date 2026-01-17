package com.example.expensetracker.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.example.expensetracker.R
import com.example.expensetracker.ui.models.MonthItem

enum class MonthEnum(val order: Int, @StringRes val displayName: Int) {
    JANUARY(1, R.string.january), FEBRUARY(2, R.string.february), MARCH(3, R.string.march),
    APRIL(4, R.string.april), MAY(5, R.string.may), JUNE(6, R.string.june),
    JULY(7, R.string.july), AUGUST(8, R.string.august), SEPTEMBER(9, R.string.september),
    OCTOBER(10, R.string.october), NOVEMBER(11, R.string.november), DECEMBER(12, R.string.december);

    companion object {
        fun fromNumber(monthNumber: Int): MonthEnum = MonthEnum.entries[monthNumber - 1]

        fun createMonthItems(context: Context): List<MonthItem> =
            listOf(MonthItem(0, context.getString(R.string.all).trim())) + entries.map {
                MonthItem(
                    it.order,
                    context.getString(it.displayName).substring(0, 3).trim()
                )
            }
    }
}