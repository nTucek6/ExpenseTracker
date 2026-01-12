package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.ExpenseEnum
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlin.getValue

class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    private val expenseViewModel: ExpenseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerCategory: Spinner = view.findViewById(R.id.spinner_category)
        val tilAmount : TextInputLayout = view.findViewById(R.id.input_amount)
        val tilDescription : TextInputLayout = view.findViewById(R.id.input_description)
        val btnSave : MaterialButton = view.findViewById(R.id.save_expense_btn)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ExpenseEnum.entries.toTypedArray()
        )
        spinnerCategory.adapter = adapter


        btnSave.setOnClickListener {
            val amount = tilAmount.editText?.text.toString().toDoubleOrNull()
            val description = tilDescription.editText?.textColors.toString();
            val selectedCategory = spinnerCategory.selectedItem as ExpenseEnum

            if (amount != null && amount > 0) {
                // Save: Expense(amount, description, selectedCategory)
                expenseViewModel.insert(amount, description, selectedCategory)
                toast("Saved ${selectedCategory.getDisplayName()}: $$amount")
                findNavController().popBackStack()
            } else {
                tilAmount.error = "Enter valid amount"
                toast("Please enter valid amount")
            }
        }

    }
}

private fun AddExpenseFragment.toast(text: String) {
    Toast.makeText(context,text, Toast.LENGTH_SHORT).show()
}
