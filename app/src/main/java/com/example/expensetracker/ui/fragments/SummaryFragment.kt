package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.ListPopupWindow
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.ExpenseEnum
import com.example.expensetracker.data.enums.MonthEnum
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.databinding.FragmentSummaryBinding
import com.example.expensetracker.ui.adapters.SummaryPagingAdapter
import com.example.expensetracker.ui.models.MonthItem
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class SummaryFragment : Fragment(R.layout.fragment_summary) {

    private val summaryViewModel: MonthlySummaryViewModel by viewModels()

    private lateinit var summaryAdapter: SummaryPagingAdapter

    private lateinit var yearAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val autoCompleteYear : AutoCompleteTextView = view.findViewById(R.id.autoCompleteYear)
        val autoCompleteMonth : AutoCompleteTextView = view.findViewById(R.id.autoCompleteMonth)


        yearAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, mutableListOf("Loading..."))
        autoCompleteYear.setAdapter(yearAdapter)

        summaryViewModel.findAllExistingYears.observe(viewLifecycleOwner) { data ->
            data?.let { yearsList ->
                val displayYears = listOf("All") + yearsList.map { it.toString() }
                yearAdapter.clear()
                yearAdapter.addAll(displayYears)
                yearAdapter.notifyDataSetChanged()
            }
        }

        val months = MonthEnum.createMonthItems(requireContext())
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, months)
        autoCompleteMonth.setAdapter(monthAdapter)

        autoCompleteMonth.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val monthItem = parent.getItemAtPosition(position) as MonthItem
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
}