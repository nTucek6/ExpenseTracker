package com.example.expensetracker.data.enums

enum class MonthEnum(val displayName: String) {
    JANUARY("January"), FEBRUARY("February"), MARCH("March"),
    APRIL("April"), MAY("May"), JUNE("June"),
    JULY("July"), AUGUST("August"), SEPTEMBER("September"),
    OCTOBER("October"), NOVEMBER("November"), DECEMBER("December");

    companion object {
        fun fromNumber(monthNumber: Int): MonthEnum = MonthEnum.entries[monthNumber - 1]
    }
}