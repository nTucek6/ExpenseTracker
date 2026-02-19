package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.LanguageISOEnum
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.NetworkViewModel
import com.example.expensetracker.utils.DialogUtils
import com.example.expensetracker.utils.SharedPreferencesUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var singInBtn: MaterialButton
    lateinit var syncDataBtn: MaterialButton
    lateinit var downloadDataBtn: MaterialButton

    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    private val networkViewModel: NetworkViewModel by activityViewModels()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleAuthClient = GoogleAuthClient(requireContext())


        val tvSingInStatus = view.findViewById<TextView>(R.id.tv_sign_in_status)
        val tvSingInInfo = view.findViewById<TextView>(R.id.tv_sign_in_info)
        val swAutoSync = view.findViewById<SwitchMaterial>(R.id.sw_auto_sync)
        val btnLanguage = view.findViewById<Button>(R.id.btn_language)

        singInBtn = view.findViewById(R.id.btn_login_out)
        syncDataBtn = view.findViewById(R.id.btn_sync_data)
        downloadDataBtn = view.findViewById(R.id.btn_download_data)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                googleAuthClient.isSignedIn.collectLatest { isSignedIn ->
                    val autoSync = SharedPreferencesUtils.getAutoSync(requireContext())
                    val user = googleAuthClient.getUser()
                    swAutoSync.isChecked = autoSync
                    tvSingInStatus.text =
                        if (isSignedIn) "${user?.displayName} (${user?.email})" else "Not signed in"
                    tvSingInInfo.text =
                        if (isSignedIn) "Connected with Google" else "Sign in with Google to sync data"
                    singInBtn.text = if (isSignedIn) "Sign Out" else "Sign in With Google"
                    syncDataBtn.isVisible = isSignedIn
                    downloadDataBtn.isVisible = isSignedIn
                    swAutoSync.isVisible = isSignedIn
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
                        expenseViewModel.deleteFromCacheCrud()
                    } else {
                        googleAuthClient.signIn()

                        val autoSync = SharedPreferencesUtils.getAutoSync(requireContext())
                        val user = googleAuthClient.getUser()
                        if (autoSync && user != null) {
                            FirebaseDb.syncData(user, expenseViewModel, summaryViewModel)
                        }

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
                    swAutoSync.isEnabled = online
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

        swAutoSync.setOnCheckedChangeListener { button, isChecked ->
            if (!button.isPressed) return@setOnCheckedChangeListener
            SharedPreferencesUtils.saveAutoSync(
                requireContext(),
                isChecked
            )
            Log.d("CheckAutoSync", isChecked.toString())
            if (isChecked) {
                viewLifecycleOwner.lifecycleScope.launch {
                    googleAuthClient.getUser()?.let { user ->
                        Log.d("CheckAutoSync", user.email.toString())
                        //lifecycleScope.launch {
                        FirebaseDb.syncData(user, expenseViewModel, summaryViewModel)
                        // }
                    }
                }
            }
        }

        btnLanguage.setOnClickListener {
            val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)

            if (currentLocale?.language == LanguageISOEnum.EN.code) {
                changeLanguage(LanguageISOEnum.CRO.code)
            } else {
                changeLanguage(LanguageISOEnum.EN.code)
            }


        }
    }

    fun syncDataDialog(user: FirebaseUser) {
        DialogUtils.showInfoConfirmation(
            context = requireContext(),
            onConfirm = {
                lifecycleScope.launch {
                    FirebaseDb.syncData(user, expenseViewModel, summaryViewModel)
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

    private fun changeLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

}