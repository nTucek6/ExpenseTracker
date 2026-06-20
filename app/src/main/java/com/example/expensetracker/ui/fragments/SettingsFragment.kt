package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.LanguageISOEnum
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.NetworkViewModel
import com.example.expensetracker.utils.DialogUtils
import com.example.expensetracker.utils.LanguageUtils
import com.example.expensetracker.utils.SharedPreferencesUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.getValue

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var singInOutBtn: MaterialButton
    lateinit var syncDataBtn: LinearLayout
    lateinit var downloadDataBtn: LinearLayout

    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    private val networkViewModel: NetworkViewModel by activityViewModels()

    @SuppressLint("UnsafeRepeatOnLifecycleDetector", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleAuthClient = GoogleAuthClient(requireContext())


        val tvAccountName = view.findViewById<TextView>(R.id.tvAccountName)
        val tvAccountEmail = view.findViewById<TextView>(R.id.tvAccountEmail)
        val tvAccountProvider = view.findViewById<TextView>(R.id.tvAccountProvider)
        val swAutoSync = view.findViewById<SwitchMaterial>(R.id.switchAutoSync)
        val btnLanguage = view.findViewById<LinearLayout>(R.id.rowLanguage)
        val tvLanguageValue = view.findViewById<TextView>(R.id.tvLanguageValue)
        val btnManageCategories = view.findViewById<LinearLayout>(R.id.rowManageCategories)

        singInOutBtn = view.findViewById(R.id.btnSignInOut)
        syncDataBtn = view.findViewById(R.id.rowSyncData)
        downloadDataBtn = view.findViewById(R.id.rowDownloadData)

        tvLanguageValue.text = LanguageUtils.getCurrentLanguageLabel(requireContext())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                googleAuthClient.isSignedIn.collectLatest { isSignedIn ->
                    val autoSync = SharedPreferencesUtils.getAutoSync(requireContext())
                    val user = googleAuthClient.getUser()
                    swAutoSync.isChecked = autoSync

                    /*tvSingInStatus.text =
                        if (isSignedIn) "${user?.displayName} (${user?.email})" else "Not signed in"
                    tvSingInInfo.text =
                        if (isSignedIn) "Connected with Google" else "Sign in with Google to sync data"*/

                    if (isSignedIn) {
                        tvAccountName.text = user?.displayName
                        tvAccountEmail.text = user?.email
                        tvAccountProvider.text = getString(R.string.connected_with_google)
                        singInOutBtn.text = getString(R.string.sign_out)
                    } else {
                        tvAccountName.text = getString(R.string.not_signed_in)
                        tvAccountEmail.text = ""
                        tvAccountProvider.text = getString(R.string.sign_in_with_google)
                        singInOutBtn.text = getString(R.string.sign_in)
                    }

                    syncDataBtn.isVisible = isSignedIn
                    downloadDataBtn.isVisible = isSignedIn
                    swAutoSync.isVisible = isSignedIn
                }
            }
        }

        singInOutBtn.setOnClickListener {
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
                    singInOutBtn.isEnabled = online
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
            DialogUtils.showLanguageDialog(requireContext())
            /*val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)

            if (currentLocale?.language == LanguageISOEnum.EN.code) {
                changeLanguage(LanguageISOEnum.CRO.code)
            } else {
                changeLanguage(LanguageISOEnum.EN.code)
            }*/
        }

        btnManageCategories.setOnClickListener {
            val navigate = SettingsFragmentDirections.actionSettingsToManageCategories()
            findNavController().navigate(navigate)
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
            title = requireContext().getString(R.string.download_data_title),
            message = requireContext().getString(R.string.download_data_message)
        )
    }

    private fun toggleButtonDisable() {
        singInOutBtn.isEnabled = !singInOutBtn.isEnabled
    }


}