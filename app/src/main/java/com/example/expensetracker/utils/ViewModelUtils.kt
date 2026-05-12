package com.example.expensetracker.utils

import android.content.Context
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient

object ViewModelUtils {

     fun checkOfflineSync(googleAuthClient: GoogleAuthClient, context: Context): Boolean {
        val autoSync = SharedPreferencesUtils.getAutoSync(context)
        val login = googleAuthClient.isSingedIn()

        return autoSync && login
    }
}