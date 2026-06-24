package com.example.expensetracker.ui.helper

import android.content.Context
import android.os.Looper
import android.widget.Toast

class SyncToastManager(
    private val context: Context
) {
    private val handler = android.os.Handler(Looper.getMainLooper())
    private var pendingCount = 0
    private var runnable: Runnable? = null
    private var currentToast: Toast? = null

    fun onItemUpdated() {
        pendingCount++

        runnable?.let { handler.removeCallbacks(it) }

        runnable = Runnable {
            currentToast?.cancel()

            val message = if (pendingCount == 1) {
                "1 expense updated"
            } else {
                "$pendingCount expenses updated"
            }

            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            currentToast?.show()

            pendingCount = 0
        }

        handler.postDelayed(runnable!!, 800)
    }
}