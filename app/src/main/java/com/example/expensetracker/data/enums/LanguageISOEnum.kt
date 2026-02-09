package com.example.expensetracker.data.enums

import androidx.annotation.StringRes
import com.example.expensetracker.R

enum class LanguageISOEnum(val code: String, @StringRes val displayName: Int) {
    EN("en", R.string.english),
    CRO("hr", R.string.croatian),
}