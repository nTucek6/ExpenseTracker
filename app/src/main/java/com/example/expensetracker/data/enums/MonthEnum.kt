package com.example.expensetracker.data.enums

import androidx.annotation.StringRes
import com.example.expensetracker.R

enum class MonthEnum(@StringRes val displayName: Int) {
    JANUARY(R.string.january), FEBRUARY(R.string.february), MARCH(R.string.march),
    APRIL(R.string.april), MAY(R.string.may), JUNE(R.string.june),
    JULY(R.string.july), AUGUST(R.string.august), SEPTEMBER(R.string.september),
    OCTOBER(R.string.october), NOVEMBER(R.string.november), DECEMBER(R.string.december);

    companion object {
        fun fromNumber(monthNumber: Int): MonthEnum = MonthEnum.entries[monthNumber - 1]
    }
}