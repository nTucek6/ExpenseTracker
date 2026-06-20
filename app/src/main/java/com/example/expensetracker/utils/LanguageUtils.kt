package com.example.expensetracker.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.expensetracker.data.enums.LanguageISOEnum
import java.util.Locale

object LanguageUtils {

    fun changeLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getCurrentLanguageCode(): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        return if (!appLocales.isEmpty) {
            appLocales[0]?.language ?: "en"
        } else {
            Locale.getDefault().language
        }
    }

    fun getCurrentLanguageLabel(context: Context): String {
        val languages = LanguageISOEnum.entries.toTypedArray()
        return context.getString(languages.first { it.code == getCurrentLanguageCode() }.displayName)
    }
}