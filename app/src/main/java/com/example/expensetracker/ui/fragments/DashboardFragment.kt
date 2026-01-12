package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expensetracker.R
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expense ->
            // Update RecyclerView
        }

        val topText = view.findViewById<TextView>(R.id.topText)
        //topText.text = topText.text.toString() + ", Nikola"

        summaryViewModel.getCurrentMonthBudget.observe(viewLifecycleOwner) {
            sum ->
            topText.text = ("Year: "+sum.year.toString() + " Month: " + sum.month.toString() + " Amount:" + sum.money)
        }


    }

  /*  private fun setupInsertButton() {
        binding.insertButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.descriptionEditText.text.toString()

            viewModel.insertExpense(amount, description)

            // Clear inputs
            binding.amountEditText.text.clear()
            binding.descriptionEditText.text.clear()
        }
    }*/

}