package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.MonthEnum
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.ui.adapters.SummaryPagingAdapter
import com.example.expensetracker.ui.models.MonthItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class SummaryFragment : Fragment(R.layout.fragment_summary) {

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()
    private lateinit var summaryAdapter: SummaryPagingAdapter
    private lateinit var yearAdapter: ArrayAdapter<MonthItem>
    private lateinit var monthAdapter: ArrayAdapter<MonthItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val autoCompleteYear: AutoCompleteTextView = view.findViewById(R.id.autoCompleteYear)
        val autoCompleteMonth: AutoCompleteTextView = view.findViewById(R.id.autoCompleteMonth)

        lifecycleScope.launch {
            val years = summaryViewModel.findAllExistingYears()
            val displayYears = listOf(MonthItem(0, getString(R.string.all))) + years.map {
                MonthItem(
                    it,
                    it.toString()
                )
            }

            yearAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, displayYears)
            autoCompleteYear.setAdapter(yearAdapter)
            autoCompleteYear.setText(getString(R.string.all), false)
        }

        autoCompleteYear.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedYear = parent.getItemAtPosition(position) as MonthItem
                summaryViewModel.updateYearQuery(selectedYear.value)
                yearAdapter.filter.filter(null)
                autoCompleteYear.dismissDropDown()
            }

        lifecycleScope.launch {
            val months = MonthEnum.createMonthItems(requireContext())
            monthAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, months)
            autoCompleteMonth.setAdapter(monthAdapter)
            autoCompleteMonth.setText(getString(R.string.all), false)
        }

        autoCompleteMonth.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedMonth = parent.getItemAtPosition(position) as MonthItem
                summaryViewModel.updateMonthQuery(selectedMonth.value)
                monthAdapter.filter.filter(null)
                autoCompleteMonth.dismissDropDown()
            }

        summaryAdapter = SummaryPagingAdapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_summary)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = summaryAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            summaryViewModel.summaryPaging.collectLatest { pagingData ->
                summaryAdapter.submitData(pagingData)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (::yearAdapter.isInitialized) yearAdapter.filter.filter(null)
        if (::monthAdapter.isInitialized) monthAdapter.filter.filter(null)
    }
}
