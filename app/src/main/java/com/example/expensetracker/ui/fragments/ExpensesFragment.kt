package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.ui.adapters.ExpensePagingAdapter
import com.example.expensetracker.utils.DialogUtils
import com.google.android.material.internal.ViewUtils.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class ExpensesFragment : Fragment(R.layout.fragment_expenses) {

    private val expenseViewModel: ExpenseViewModel by viewModels()

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    private lateinit var expenseAdapter: ExpensePagingAdapter
    private lateinit var searchView: SearchView
    private var allExpenses: List<Expense> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        val rootLayout = view.findViewById<ConstraintLayout>(R.id.root_expense)

        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Hide keyboard and collapse SearchView
                hideKeyboard(searchView)
                searchView.apply {
                    setQuery("", false)
                    isIconified = true
                    clearFocus()
                }
            }
            false
        }
        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.findFocus()
        }

        expenseAdapter = ExpensePagingAdapter { expense -> showExpenseDialog(expense) }
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_expenses)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = expenseAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                expenseViewModel.updateQuery(newText ?: "")
                return false
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.expensesPaging.collectLatest { pagingData ->
                expenseAdapter.submitData(pagingData)
            }
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
                val action = ExpensesFragmentDirections.actionExpensesToEditExpense(expense)
                findNavController().navigate(action)
            })
    }
}