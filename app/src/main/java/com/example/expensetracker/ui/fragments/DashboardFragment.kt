package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expensetracker.R
import com.example.expensetracker.data.viewModel.ExpenseViewModel

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    //private lateinit var viewModel: ExpenseViewModel
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allExpenses.observe(viewLifecycleOwner) { expense ->
            // Update RecyclerView
        }

        val topText = view.findViewById<TextView>(R.id.topText)
        topText.text = topText.text.toString() + ", Nikola"
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