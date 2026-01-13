package com.example.expensetracker.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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