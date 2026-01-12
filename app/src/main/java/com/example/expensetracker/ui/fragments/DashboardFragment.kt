package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.enums.GreetingsEnum
import com.example.expensetracker.data.enums.MonthEnum
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.ui.adapters.ExpenseAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Date
import java.util.Locale
import kotlin.math.exp

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topText = view.findViewById<TextView>(R.id.tv_top_text)
        val dateText = view.findViewById<TextView>(R.id.tv_date)
        val totalSpentText = view.findViewById<TextView>(R.id.tv_total_spent)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val spentAmountText = view.findViewById<TextView>(R.id.tvSpentAmount)
        val remainingText = view.findViewById<TextView>(R.id.tvRemainingAmount)

        topText.text = GreetingsEnum.now().displayName;

        expenseViewModel.totalSpent.observe(viewLifecycleOwner) {
            total ->
            totalSpentText.text = total.toString() + "€"
        }

        summaryViewModel.getCurrentMonthBudget.observe(viewLifecycleOwner) { sum ->
            dateText.text =
                (MonthEnum.fromNumber(sum.month).displayName + " " + sum.year.toString())
            spentAmountText.text = sum.spent.toString() + "€"
            remainingText.text = (sum.money - sum.spent).toString() + "€"
        }

        val expenseAdapter = ExpenseAdapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_recent_expenses)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = expenseAdapter

        expenseViewModel.recentExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.newExpenseBtn).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_new_expense)
        }
    }
}