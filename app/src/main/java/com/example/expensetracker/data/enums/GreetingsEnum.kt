package com.example.expensetracker.data.enums

import androidx.annotation.StringRes
import com.example.expensetracker.R
import java.util.Calendar

enum class GreetingsEnum(@StringRes val displayName: Int) {
    MORNING(R.string.morning),
    AFTERNOON(R.string.afternoon),
    EVENING(R.string.evening),
    NIGHT(R.string.night);

    companion object {
        fun now(): GreetingsEnum {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..11 -> MORNING
                in 12..15 -> AFTERNOON
                in 16..20 -> EVENING
                else -> NIGHT
            }
        }
    }

}