package com.example.expensetracker.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

object DialogUtils {

    fun showExpenseDialog(
        context: Context,
        expense: Expense,
        onDelete: () -> Unit,
        onEdit: (Expense) -> Unit
    ) {
        val dialog: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_edit_expense_layout, null)

        dialog.findViewById<TextView>(R.id.tv_category).text = String.format(
            context.getString(R.string.category_info_format),
            expense.category.displayName
        )
        dialog.findViewById<TextView>(R.id.tv_description).text = String.format(
            context.getString(R.string.description_info_format),
            expense.description
        )
        dialog.findViewById<TextView>(R.id.tv_amount).text = String.format(
            context.getString(R.string.amount_info_format),
            expense.amount
        )
        dialog.findViewById<TextView>(R.id.tv_date).text = String.format(
            context.getString(R.string.date_time_info_format),
            expense.createdAt.toDateTimeString()
        )
        MaterialAlertDialogBuilder(context)
            .setTitle("Expense Details")
            .setView(dialog)
            .setNegativeButton("Close", null)
            .setNeutralButton("Delete") { _, _ -> onDelete() }
            .setPositiveButton("Edit") { _, _ -> onEdit(expense) }
            .show()
    }

    fun showDeleteConfirmation(
        context: Context,
        onConfirm: () -> Unit,
        title: String? = "Confirm Delete",
        message: String? = "Delete this item? This can't be undone.",
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> onConfirm() }
            .show()
    }


    fun showEditLimitDialog(
        context: Context,
        money: Double,
        onConfirm: (Double) -> Unit,
    ) {
        val dialog: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_edit_limit_layout, null)
        val tlEditLimit = dialog.findViewById<TextInputLayout>(R.id.til_edit_limit)
        tlEditLimit.editText?.setText(money.toString())
        MaterialAlertDialogBuilder(context)
            .setTitle("Edit limit")
            .setView(dialog)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update") { _, _ ->
                val editLimit = tlEditLimit.editText?.text.toString().toDoubleOrNull()
                if (editLimit != null && editLimit >= 0) {
                    onConfirm(editLimit)
                }
            }
            .show()
    }

}