package com.example.expensetracker.ui.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce

class NetworkViewModel(application: Application) : AndroidViewModel(application) {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    @OptIn(FlowPreview::class)
    val isOnlineDebounced = isOnline.debounce(1000L)

    init {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
            }
            override fun onLost(network: Network) {
                _isOnline.value = false
            }
        }
        cm.registerDefaultNetworkCallback(callback)
    }
}