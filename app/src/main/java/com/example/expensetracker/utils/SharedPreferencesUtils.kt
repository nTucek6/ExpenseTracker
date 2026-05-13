package com.example.expensetracker.utils

import android.content.Context

import androidx.core.content.edit
import com.example.expensetracker.R

object SharedPreferencesUtils {

    fun isFirstLaunch(context: Context): Boolean{
        val sharedPref = context.getSharedPreferences(context.getString(R.string.app_prefs),Context.MODE_PRIVATE)
        return sharedPref.getBoolean(context.getString(R.string.pref_is_first_launch), true)
    }

    fun disableFirstLaunch(context: Context){
        val sharedPref = context.getSharedPreferences(context.getString(R.string.app_prefs), Context.MODE_PRIVATE) ?: return
        sharedPref.edit {
            putBoolean(context.getString(R.string.pref_is_first_launch), false)
        }
    }

    fun saveAutoSync(context: Context, value: Boolean) {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.pref_settings), Context.MODE_PRIVATE) ?: return
        sharedPref.edit {
             putBoolean(context.getString(R.string.saved_auto_sync), value)
        }
    }

    fun getAutoSync(context: Context) : Boolean{
        val sharedPref = context.getSharedPreferences(context.getString(R.string.pref_settings),Context.MODE_PRIVATE)
        return sharedPref.getBoolean(context.getString(R.string.saved_auto_sync), false)
    }


}