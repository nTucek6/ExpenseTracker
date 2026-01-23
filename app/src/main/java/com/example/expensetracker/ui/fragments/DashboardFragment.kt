package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.example.expensetracker.utils.DialogUtils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    private val summaryViewModel: MonthlySummaryViewModel by activityViewModels()

    private var money: Double = 0.0

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topText = view.findViewById<TextView>(R.id.tv_top_text)
        val dateText = view.findViewById<TextView>(R.id.tv_date)
        val totalSpentText = view.findViewById<TextView>(R.id.tv_total_spent)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val spentAmountText = view.findViewById<TextView>(R.id.tvSpentAmount)
        val remainingText = view.findViewById<TextView>(R.id.tvRemainingAmount)

        val greetingText = GreetingsEnum.now().displayName.let { resId ->
            requireContext().getString(resId)
        }
        topText.text = greetingText

        expenseViewModel.totalSpent.observe(viewLifecycleOwner) { total ->
            totalSpentText.text = String.format(getString(R.string.price_format), total)
        }

        summaryViewModel.getCurrentMonthBudget.observe(viewLifecycleOwner) { sum ->
            dateText.text = String.format(
                getString(R.string.dashboard_date_format),
                MonthEnum.fromNumber(1).displayName.let { requireContext().getString(it) },//MonthEnum.fromNumber(sum.month).displayName,
                sum.year.toString()
            )

            spentAmountText.text = String.format(getString(R.string.price_format), sum.spent)

            val remaining = (sum.money - sum.spent)
            remainingText.text = String.format(getString(R.string.price_format), remaining)
            if (sum.money > 0 && remaining > 0) {
                val progress = ((remaining / sum.money) * 100).toInt().coerceIn(0, 100);
                progressBar.progress = 100 - progress
            } else {
                progressBar.progress = 0
            }
            if (sum.money >= 0) {
                money = sum.money
            }
        }

        val expenseAdapter = ExpenseAdapter { expense -> showExpenseDialog(expense) }
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_recent_expenses)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = expenseAdapter

        expenseViewModel.recentExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
        }

        view.findViewById<MaterialCardView>(R.id.mcv_editLimit).setOnClickListener {
            showEditLimitDialog()
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.newExpenseBtn).setOnClickListener {
            val navigate = DashboardFragmentDirections.actionDashboardToNewExpense(null)
            findNavController().navigate(navigate)
        }
    }

    private fun showExpenseDialog(
        expense: Expense
    ) {
        DialogUtils.showExpenseDialog(
            context = requireContext(),
            expense = expense,
            onDelete = {
                DialogUtils.showDeleteConfirmation(
                    context = requireContext(),
                    onConfirm = { expenseViewModel.delete(expense) })
            },
            onEdit = {
                val action = DashboardFragmentDirections.actionDashboardToNewExpense(expense)
                findNavController().navigate(action)
            })
    }

    private fun showEditLimitDialog() {
        DialogUtils.showEditLimitDialog(
            context = requireContext(),
            money = money,
            onConfirm = { editLimit ->
                summaryViewModel.updateLatestMonth(editLimit)
            })
    }
}