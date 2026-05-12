package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.viewModel.CategoriesViewModel
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.ui.models.DropdownItem
import com.example.expensetracker.utils.toMillisDate
import com.example.expensetracker.utils.todayCalendarToMillis
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.getValue

class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    private val args: AddExpenseFragmentArgs by navArgs()
    private val expense: Expense? get() = args.expense

    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    private val categoryViewModel: CategoriesViewModel by activityViewModels()

    private lateinit var categoryAdapter: ArrayAdapter<DropdownItem>

    var selectedCategory: DropdownItem? = null;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        //val spinnerCategory: Spinner = view.findViewById(R.id.spinner_category)
        val spinnerCategory: AutoCompleteTextView = view.findViewById(R.id.spinner_category)
        val tilAmount: TextInputLayout = view.findViewById(R.id.input_amount)
        val tilDescription: TextInputLayout = view.findViewById(R.id.input_description)
        val btnSave: MaterialButton = view.findViewById(R.id.save_expense_btn)

        val datePicker: DatePicker = view.findViewById(R.id.datePicker)
        val timePicker: TimePicker = view.findViewById(R.id.timePicker)

        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(requireContext()))

        datePicker.maxDate = Calendar.getInstance().todayCalendarToMillis()

        lifecycleScope.launch {
            val categories = categoryViewModel.allCategories
                .asFlow()
                .filter { it.isNotEmpty() }
                .first()
            val displayCategories = categories.map {
                DropdownItem(
                    value = it.id,
                    name = it.displayName
                )
            }
            categoryAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, displayCategories)
            spinnerCategory.setAdapter(categoryAdapter)

            val data = expense;
            if (data != null) {
                Log.d("OnUpdate", data.categoryId.toString())
                tvTitle.text = getString(R.string.edit_expense)
                tilAmount.editText?.setText(data.amount.toString())
                val selectedItem = displayCategories.firstOrNull() { it -> it.value == data.categoryId }
                selectedItem?.let {
                    spinnerCategory.setText(it.name, false)
                }
                selectedCategory = selectedItem
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
        }

        spinnerCategory.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedCategory = parent.getItemAtPosition(position) as DropdownItem
                categoryAdapter.filter.filter(null)
                // autoCompleteYear.dismissDropDown()
            }

        btnSave.setOnClickListener {
            val amount = tilAmount.editText?.text.toString().toDoubleOrNull()
            val description = tilDescription.editText?.text.toString();

            val createdAt = datePicker.toMillisDate(timePicker)

            if (amount != null && amount > 0 && selectedCategory != null) {
                if (expense != null) {
                    expenseViewModel.update(
                        expense!!.id,
                        amount,
                        description,
                        selectedCategory!!.value,
                        createdAt
                    )
                    toast("Updated ${selectedCategory!!.name}: $amount€")

                } else {
                    Log.d("ExpenseUpdate", "insert?")

                    expenseViewModel.insert(amount, description, selectedCategory!!.value, createdAt)
                    toast("Saved ${selectedCategory!!.name}: $amount€")
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