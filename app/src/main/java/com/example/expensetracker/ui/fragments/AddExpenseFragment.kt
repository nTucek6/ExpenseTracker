package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.ExpenseEnum
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.utils.toMillisDate
import com.example.expensetracker.utils.todayCalendarToMillis
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import kotlin.getValue

class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    private val args: AddExpenseFragmentArgs by navArgs()
    private val expense: Expense? get() = args.expense

    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val spinnerCategory: Spinner = view.findViewById(R.id.spinner_category)
        val tilAmount: TextInputLayout = view.findViewById(R.id.input_amount)
        val tilDescription: TextInputLayout = view.findViewById(R.id.input_description)
        val btnSave: MaterialButton = view.findViewById(R.id.save_expense_btn)

        val datePicker: DatePicker = view.findViewById(R.id.datePicker)
        val timePicker: TimePicker = view.findViewById(R.id.timePicker)

        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(requireContext()))

        datePicker.maxDate = Calendar.getInstance().todayCalendarToMillis()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ExpenseEnum.entries.toTypedArray()
        )
        spinnerCategory.adapter = adapter

        val data = expense;
        if (data != null) {
            tvTitle.text = getString(R.string.edit_expense)
            tilAmount.editText?.setText(data.amount.toString())
            val position = getCategoryPosition(data.category)
            if (position >= 0) {
                spinnerCategory.setSelection(position)
            }
            tilDescription.editText?.setText(data.description.toString())

            val date = Calendar.getInstance()
            date.timeInMillis = data.createdAt

            datePicker.updateDate(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            )
            timePicker.hour = date.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = date.get(Calendar.MINUTE)

        }

        btnSave.setOnClickListener {
            val amount = tilAmount.editText?.text.toString().toDoubleOrNull()
            val description = tilDescription.editText?.text.toString();
            val selectedCategory = spinnerCategory.selectedItem as ExpenseEnum

            val createdAt = datePicker.toMillisDate(timePicker)

            if (amount != null && amount > 0) {
                if (expense != null) {
                    expenseViewModel.update(
                        expense!!.id,
                        amount,
                        description,
                        selectedCategory,
                        createdAt
                        //expense!!.createdAt
                    )
                    toast("Updated ${selectedCategory.displayName}: $amount€")

                } else {
                    Log.d("ExpenseUpdate", "insert?")

                    expenseViewModel.insert(amount, description, selectedCategory, createdAt)
                    toast("Saved ${selectedCategory.displayName}: $amount€")
                }
                findNavController().popBackStack()
            } else {
                tilAmount.error = "Enter valid amount"
            }
        }
    }
}

private fun AddExpenseFragment.toast(text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

private fun getCategoryPosition(category: ExpenseEnum): Int {
    return ExpenseEnum.entries.toTypedArray().indexOfFirst { it == category }
}