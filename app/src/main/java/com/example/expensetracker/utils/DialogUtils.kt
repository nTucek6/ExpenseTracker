package com.example.expensetracker.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.LanguageISOEnum
import com.example.expensetracker.data.model.ManageCategories
import com.example.expensetracker.utils.LanguageUtils.changeLanguage
import com.example.expensetracker.utils.LanguageUtils.getCurrentLanguageCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

object DialogUtils {

    fun showExpenseDialog(
        context: Context,
        expense: Expense,
        category: String,
        onDelete: () -> Unit,
        onEdit: (Expense) -> Unit
    ) {
        val dialog: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_edit_expense_layout, null)

        dialog.findViewById<TextView>(R.id.tv_category).text = String.format(
            context.getString(R.string.category_info_format),
            //expense.category.displayName
            category
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
            .setTitle(context.getString(R.string.expense_details))
            .setView(dialog)
            .setNegativeButton(context.getString(R.string.close), null)
            .setNeutralButton(context.getString(R.string.delete)) { _, _ -> onDelete() }
            .setPositiveButton(context.getString(R.string.edit)) { _, _ -> onEdit(expense) }
            .show()
    }

    fun showDeleteConfirmation(
        context: Context,
        onConfirm: () -> Unit,
        title: String? = context.getString(R.string.confirm_delete),
        message: String? = context.getString(R.string.delete_message),
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(context.getString(R.string.cancel), null)
            .setPositiveButton(context.getString(R.string.delete)) { _, _ -> onConfirm() }
            .show()
    }

    fun showInfoConfirmation(
        context: Context,
        onConfirm: () -> Unit,
        title: String? = context.getString(R.string.confirm_delete),
        message: String? = context.getString(R.string.delete_message),
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(context.getString(R.string.cancel), null)
            .setPositiveButton(context.getString(R.string.continue_dialog)) { _, _ -> onConfirm() }
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
            .setTitle(context.getString(R.string.edit_limit))
            .setView(dialog)
            .setNegativeButton(context.getString(R.string.cancel), null)
            .setPositiveButton(context.getString(R.string.update)) { _, _ ->
                val editLimit = tlEditLimit.editText?.text.toString().toDoubleOrNull()
                if (editLimit != null && editLimit >= 0) {
                    onConfirm(editLimit)
                }
            }
            .show()
    }

    fun showAddCategoryDialog(
        context: Context,
        onAdd: (String) -> Unit,
    ) {
        val dialog: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_edit_category, null)
        val tlAddCategory = dialog.findViewById<TextInputLayout>(R.id.til_edit_category)
        tlAddCategory.hint = context.getString(R.string.add_category)
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.add_category))
            .setView(dialog)
            .setNegativeButton(context.getString(R.string.close), null)
            .setPositiveButton(context.getString(R.string.edit)) { _, _ ->
                val addCategory = tlAddCategory.editText?.text.toString()
                onAdd(addCategory)
            }
            .show()
    }

    fun showEditCategoryDialog(
        context: Context,
        category: ManageCategories,
        onEdit: (ManageCategories) -> Unit,
        onDelete: (Int) -> Unit,
    ) {
        val dialog: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_edit_category, null)

        val tlEditCategory = dialog.findViewById<TextInputLayout>(R.id.til_edit_category)

        tlEditCategory.hint = context.getString(R.string.edit_category)
        tlEditCategory.editText?.setText(category.displayName)

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.edit_category))
            .setView(dialog)
            .setNegativeButton(context.getString(R.string.close), null)
            .setNeutralButton(context.getString(R.string.delete)) { _, _ -> onDelete(category.id) }
            .setPositiveButton(context.getString(R.string.edit)) { _, _ ->
                val editCategory = tlEditCategory.editText?.text.toString()
                val editedCategory = ManageCategories(
                    id = category.id,
                    displayName = editCategory,
                    isDefault = category.isDefault,
                    imageSvg = category.imageSvg,
                    expensesCount = category.expensesCount
                )
                onEdit(editedCategory)
            }
            .show()
    }

    fun showLanguageDialog(context: Context) {
        val languages = LanguageISOEnum.entries.toTypedArray()
        val items = languages.map { context.getString(it.displayName) }.toTypedArray()
        val currentCode = getCurrentLanguageCode()
        val checkedItem = languages.indexOfFirst { it.code == currentCode }.takeIf { it >= 0 } ?: 0

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.choose_language))
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                val selectedCode = languages[which]
                changeLanguage(selectedCode.code)
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }
}