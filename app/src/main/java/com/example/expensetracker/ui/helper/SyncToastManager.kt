package com.example.expensetracker.ui.helper

import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.example.expensetracker.R

class SyncToastManager(
    private val context: Context
) {
    private val handler = android.os.Handler(Looper.getMainLooper())
    private var pendingCount = 0
    private var runnable: Runnable? = null
    private var currentToast: Toast? = null

    fun onExpenseUpdated() {
        pendingCount++

        runnable?.let { handler.removeCallbacks(it) }

        runnable = Runnable {
            currentToast?.cancel()

            val message = if (pendingCount == 1) {
                String.format(context.getString(R.string.updated_expenses), 1.toString())
            } else {
                String.format(context.getString(R.string.updated_expenses), pendingCount.toString())
            }

            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            currentToast?.show()

            pendingCount = 0
        }

        handler.postDelayed(runnable!!, 800)
    }

    fun onCategoryUpdated() {
        pendingCount++

        runnable?.let { handler.removeCallbacks(it) }

        runnable = Runnable {
            currentToast?.cancel()

            val message = if (pendingCount == 1) {
                String.format(context.getString(R.string.updated_categories), 1.toString())
            } else {
                String.format(
                    context.getString(R.string.updated_categories),
                    pendingCount.toString()
                )
            }
            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            currentToast?.show()
            pendingCount = 0
        }
        handler.postDelayed(runnable!!, 800)
    }

}