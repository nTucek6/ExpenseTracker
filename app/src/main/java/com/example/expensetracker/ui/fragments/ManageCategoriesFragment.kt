package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.viewModel.CategoriesViewModel
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.ui.adapters.CategoryPagingAdapter
import com.example.expensetracker.ui.adapters.ExpensePagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue


class ManageCategoriesFragment : Fragment(R.layout.fragment_manage_categories) {

    private val categoryViewModel: CategoriesViewModel by activityViewModels()

    private lateinit var categoryAdapter: CategoryPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        categoryAdapter = CategoryPagingAdapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            categoryViewModel.categoryPaging.collectLatest { pagingData ->
                //Log.d("CategoriesData", pagingData.map { it.displayName }.toString())
                categoryAdapter.submitData(pagingData)

                Log.d("CategoriesData", categoryAdapter.snapshot().items.joinToString {
                    it.displayName
                })
            }
        }

    }

}