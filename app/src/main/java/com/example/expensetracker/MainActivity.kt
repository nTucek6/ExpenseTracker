package com.example.expensetracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.firebase.database.FirebaseDb
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.example.expensetracker.ui.viewModel.NetworkViewModel
import com.example.expensetracker.utils.SharedPreferencesUtils
import kotlinx.coroutines.launch
import kotlin.getValue


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var backPressTime: Long = 0
    private val DOUBLE_PRESS_INTERVAL = 2000L

    private lateinit var navHostFragment: NavHostFragment
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val summaryViewModel: MonthlySummaryViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        val googleAuthClient = GoogleAuthClient(this)

        lifecycleScope.launch {
            summaryViewModel.createDefaultSummary()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

         navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

       /* bottomNav.setOnItemSelectedListener { item ->
            val wasHandled = navController.popBackStack(item.itemId, inclusive = false)
            if (!wasHandled) {
                navController.navigate(item.itemId)
            }
            true
        } */

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkViewModel.isOnlineDebounced.collect { online ->
                    if (online) {
                        val autoSync = SharedPreferencesUtils.getAutoSync(this@MainActivity)
                        val isSignedIn = googleAuthClient.isSignedIn.value
                        if (isSignedIn && autoSync) {
                            val user = googleAuthClient.getUser()
                            if (user != null) {
                                FirebaseDb.syncData(user, expenseViewModel, summaryViewModel)
                            }
                        }
                    }
                }
            }
        }


        setUpBackHandler()
    }

  /*  fun setUpBackHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val callback = OnBackInvokedCallback {
                exitStrategy()
                //handleBackPress()
            }
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                callback
            )
        } else {
            // Legacy: AndroidX OnBackPressedDispatcher
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitStrategy()
                    //handleBackPress()
                }
            }
            onBackPressedDispatcher.addCallback(this, callback)
        }
    } */

    private fun setUpBackHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { handleSmartBack() }
        } else {
            onBackPressedDispatcher.addCallback(this) {
                handleSmartBack()
            }
        }
    }


    private fun handleSmartBack() {
        val navController = navHostFragment.navController

        if (navController.currentDestination?.id == R.id.dashboardFragment) {
            exitStrategy()
        } else {
            navController.navigate(R.id.dashboardFragment) {
                popUpTo(R.id.dashboardFragment) { inclusive = true }
            }
        }
    }

    private fun exitStrategy() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressTime < DOUBLE_PRESS_INTERVAL) {
            finish()
        } else {
            backPressTime = currentTime
            Toast.makeText(this, getString(R.string.exit_text), Toast.LENGTH_SHORT).show()
        }
    }
}
