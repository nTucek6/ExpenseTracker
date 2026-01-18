package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.entity.MonthlySummary
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.database.mappers.toFirebaseExpenses
import com.example.expensetracker.firebase.database.mappers.toFirebaseMonthlySummary
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.getValue
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var singInBtn: MaterialButton
    lateinit var syncDataBtn: MaterialButton

    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleAuthClient = GoogleAuthClient(requireContext())
        val firebaseDb = FirebaseDb()

        singInBtn = view.findViewById(R.id.btn_login_out)
        syncDataBtn = view.findViewById(R.id.btn_sync_data)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                googleAuthClient.isSignedIn.collectLatest { isSignedIn ->
                    singInBtn.text = if (isSignedIn) "Sign Out" else "Sign in With Google"
                    syncDataBtn.isVisible = isSignedIn
                }
            }
        }

        singInBtn.setOnClickListener {
            lifecycleScope.launch {
                toggleButtonDisable()
                try {
                    val isSignedIn = googleAuthClient.isSignedIn.value
                    if (isSignedIn) {
                        googleAuthClient.signOut()
                    } else {
                        googleAuthClient.signIn()
                    }
                } finally {
                    toggleButtonDisable()
                }
            }
        }

        syncDataBtn.setOnClickListener {
            lifecycleScope.launch {
                googleAuthClient.getUser()?.let { user ->
                    try {
                        syncDataBtn.isEnabled = false
                        toggleButtonDisable()
                        val expenseList = expenseViewModel.allExpenses
                            .asFlow()
                            .filter { it.isNotEmpty() }
                            .first()
                        val summaryList = summaryViewModel.getAllMonthBudget
                            .asFlow()
                            .filter { it.isNotEmpty() }
                            .first()

                        val firebaseExpenses = expenseList.toFirebaseExpenses()
                        val firebaseSummaries = summaryList.toFirebaseMonthlySummary()
                        firebaseDb.syncData(user, firebaseExpenses, firebaseSummaries)

                    } catch (e: Exception) {
                        Log.e("FirebaseCheck", "Sync failed", e)
                    } finally {
                        toggleButtonDisable()
                        syncDataBtn.isEnabled = true
                    }
                }
            }
        }

       /* lifecycleScope.launch {
           // expenseViewModel.syncFirebaseToRoom()
            //summaryViewModel.syncFirebaseToRoom()
        } */

    }
    private fun toggleButtonDisable() {
        singInBtn.isEnabled = !singInBtn.isEnabled
    }
}