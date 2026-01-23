package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.database.mappers.toFirebaseExpenses
import com.example.expensetracker.firebase.database.mappers.toFirebaseMonthlySummary
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.NetworkViewModel
import com.example.expensetracker.utils.DialogUtils
import com.example.expensetracker.utils.SharedPreferencesUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.getValue

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var singInBtn: MaterialButton
    lateinit var syncDataBtn: MaterialButton
    lateinit var downloadDataBtn: MaterialButton
    var firebaseDb = FirebaseDb()

    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    private val networkViewModel: NetworkViewModel by viewModels()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleAuthClient = GoogleAuthClient(requireContext())

        val autoSync = SharedPreferencesUtils.getAutoSync(requireContext())


        val tvSingInStatus = view.findViewById<TextView>(R.id.tv_sign_in_status)
        val tvSingInInfo = view.findViewById<TextView>(R.id.tv_sign_in_info)
        val swAutoSync = view.findViewById<SwitchMaterial>(R.id.sw_auto_sync)

        singInBtn = view.findViewById(R.id.btn_login_out)
        syncDataBtn = view.findViewById(R.id.btn_sync_data)
        downloadDataBtn = view.findViewById(R.id.btn_download_data)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                googleAuthClient.isSignedIn.collectLatest { isSignedIn ->
                    val user = googleAuthClient.getUser()
                    swAutoSync.isChecked = autoSync
                    tvSingInStatus.text =
                        if (isSignedIn) "${user?.displayName} (${user?.email})" else "Not signed in"
                    tvSingInInfo.text =
                        if (isSignedIn) "Connected with Google" else "Sign in with Google to sync data"
                    singInBtn.text = if (isSignedIn) "Sign Out" else "Sign in With Google"
                    syncDataBtn.isVisible = isSignedIn
                    downloadDataBtn.isVisible = isSignedIn
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
                        syncDataDialog(user)
                    } catch (e: Exception) {
                        Log.e("FirebaseCheck", "Sync failed", e)
                    } finally {
                        toggleButtonDisable()
                        syncDataBtn.isEnabled = true
                    }
                }
            }
        }
        downloadDataBtn.setOnClickListener {
            lifecycleScope.launch {
                downloadDataBtn.isEnabled = false
                downloadDataDialog()
                downloadDataBtn.isEnabled = true
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkViewModel.isOnline.collect { online ->
                    singInBtn.isEnabled = online
                    syncDataBtn.isEnabled = online
                    downloadDataBtn.isEnabled = online
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkViewModel.isOnlineDebounced.collect { online ->
                    if (!online) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        swAutoSync.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferencesUtils.saveAutoSync(
                requireContext(),
                isChecked
            )
            if (isChecked) {
                lifecycleScope.launch {
                    googleAuthClient.getUser()?.let { user ->
                        lifecycleScope.launch {
                            syncData(user)
                        }
                    }
                }
            }
        }
    }


    fun syncDataDialog(user: FirebaseUser) {
        DialogUtils.showInfoConfirmation(
            context = requireContext(),
            onConfirm = {
                lifecycleScope.launch {
                    syncData(user)
                }
            },
            title = requireContext().getString(R.string.sync_data_title),
            message = requireContext().getString(R.string.sync_data_message)
        )
    }

    fun downloadDataDialog() {
        DialogUtils.showInfoConfirmation(
            context = requireContext(),
            onConfirm = {
                expenseViewModel.syncFirebaseToRoom()
                summaryViewModel.syncFirebaseToRoom()
            },
            title = requireContext().getString(R.string.download_data),
            message = requireContext().getString(R.string.download_data_message)
        )
    }

    private fun toggleButtonDisable() {
        singInBtn.isEnabled = !singInBtn.isEnabled
    }

    suspend fun syncData(user: FirebaseUser) {
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
    }
}