package com.example.expensetracker.data.repository

import com.google.firebase.auth.FirebaseUser

interface SyncRepository {

    suspend fun syncRecentUpdates(user: FirebaseUser);
    suspend fun syncData(user: FirebaseUser)
}