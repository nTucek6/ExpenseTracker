package com.example.expensetracker

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var backPressTime: Long = 0
    private val DOUBLE_PRESS_INTERVAL = 2000L

    private lateinit var viewModel: MonthlySummaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setUpBackHandler()

        viewModel = ViewModelProvider(this)[MonthlySummaryViewModel::class.java]

        lifecycleScope.launch {
            viewModel.createDefaultSummary()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        bottomNav.setOnItemSelectedListener { item ->
            val wasHandled = navController.popBackStack(item.itemId, inclusive = false)
            if (!wasHandled) {
                navController.navigate(item.itemId)
            }
            true
        }

    }

    fun setUpBackHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val callback = OnBackInvokedCallback {
                exitStrategy()
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
                }
            }
            onBackPressedDispatcher.addCallback(this, callback)
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
