package com.example.expensetracker.utils

import android.widget.DatePicker
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun Long.toDateString(pattern: String = "dd.MM.yyyy."): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}

fun Long.toTimeString(pattern: String = "HH:mm"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}

fun Long.toDateTimeString(pattern: String = "dd.MM.yyyy. HH:mm"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}


fun Long.isToday(): Boolean {
    val today = LocalDate.now(ZoneId.systemDefault())
    val timestampDate = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return timestampDate == today
}

fun Long.toLocalDate(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

fun DatePicker.toMillisDate(time: TimePicker): Long {
    val calendar = Calendar.getInstance()
    calendar.set(
        this.year,
        this.month,
        this.dayOfMonth,
        time.hour, time.minute, 0
    )
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun Calendar.todayCalendarToMillis(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun Calendar.firstOfMonthCalendarToMillis(): Long {
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()
    return today
        .withDayOfMonth(1)
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
}

fun Calendar.lastOfMonthCalendarToMillis(): Long {
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()
    return today
        .with(TemporalAdjusters.lastDayOfMonth())
        .atTime(LocalTime.MAX)
        .atZone(zone)
        .toInstant()
        .toEpochMilli()
}


fun Calendar.getLast3MonthsRange(): Pair<Long, Long> {
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()

    val startOfCurrentMonth = today.atStartOfDay(zone).toInstant().toEpochMilli()

    val threeMonthsBackMonth = today.minusMonths(3).withDayOfMonth(1)
    val startOfThreeMonthsBackMonth =
        threeMonthsBackMonth.atStartOfDay(zone).toInstant().toEpochMilli()

    return startOfThreeMonthsBackMonth to startOfCurrentMonth
}

fun Calendar.getLast6MonthsRange(): Pair<Long, Long> {
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()

    val startOfCurrentMonth = today.atStartOfDay(zone).toInstant().toEpochMilli()

    val sixMonthsBackMonth = today.minusMonths(6).withDayOfMonth(1)
    val startOfThreeMonthsBackMonth =
        sixMonthsBackMonth.atStartOfDay(zone).toInstant().toEpochMilli()
    return startOfThreeMonthsBackMonth to startOfCurrentMonth
}

fun Calendar.getLastYearRange(): Pair<Long, Long> {
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()

    val startOfCurrentMonth = today.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()

    val oneYearBack = today.minusYears(1).withDayOfMonth(1)
    val startOfThreeMonthsBackMonth =
        oneYearBack.atStartOfDay(zone).toInstant().toEpochMilli()
    return startOfThreeMonthsBackMonth to startOfCurrentMonth
}

fun formatWeekDate(weekStart: Long, weekEnd: Long): String {
    val dateFrom = Instant.ofEpochMilli(weekStart)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val dateTo = Instant.ofEpochMilli(weekEnd)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val formatter = DateTimeFormatter.ofPattern("MMM d")

    val label = if (dateFrom.month == dateTo.month) {
        "${dateFrom.format(formatter)}-${dateTo.dayOfMonth}"
    } else {
        "${dateFrom.format(formatter)} - ${dateTo.format(formatter)}"
    }
    return label
}

