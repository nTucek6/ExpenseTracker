package com.example.expensetracker.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.example.expensetracker.R


enum class PeriodChipEnum(@StringRes val displayName: Int) {
    THIS_MONTH(R.string.this_month),
    THREE_MONTHS(R.string.three_months),
    SIX_MONTHS(R.string.six_months),
    YEAR(R.string.year),
    CUSTOM(R.string.custom);

    companion object {
        fun fromText(context: Context, text: String): PeriodChipEnum {
            return PeriodChipEnum.entries.firstOrNull {
                it.toString() == text
            } ?: THIS_MONTH
        }
    }
}