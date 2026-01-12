package com.example.expensetracker.data.enums

import java.util.Calendar

enum class GreetingsEnum(val displayName: String) {
    MORNING("Good Morning"),
    AFTERNOON("Good Afternoon"),
    EVENING("Good Evening"),
    NIGHT("Night");

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