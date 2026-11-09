package com.example.expensetracker.di

import com.example.expensetracker.BuildConfig
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideDatabaseRoot(): DatabaseReference {
        val root = FirebaseDatabase.getInstance().reference

        return if (BuildConfig.DEBUG) {
            root.child("development")
        } else {
            root
        }
    }
}