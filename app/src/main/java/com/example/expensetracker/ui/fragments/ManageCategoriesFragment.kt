package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.viewModel.CategoriesViewModel
import com.example.expensetracker.ui.adapters.CategoryPagingAdapter
import com.example.expensetracker.utils.DialogUtils
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue


class ManageCategoriesFragment : Fragment(R.layout.fragment_manage_categories) {

    private val categoryViewModel: CategoriesViewModel by activityViewModels()

    private lateinit var categoryAdapter: CategoryPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newCategoryBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.newCategoryBtn)

        categoryAdapter = CategoryPagingAdapter { category -> showDeleteCategoryDialog(category) }
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

        newCategoryBtn.setOnClickListener {
            showEditCategoryDialog()
        }
    }

    private fun showEditCategoryDialog() {
        lifecycleScope.launch {
            DialogUtils.showNewCategoryDialog(
                context = requireContext(),
                onEdit = { name ->
                    categoryViewModel.insert(displayName = name, null)
                }
            )
        }
    }

    private fun showDeleteCategoryDialog(
        category: Categories
    ) {
        lifecycleScope.launch {
            val data = categoryViewModel.findById(category.id)

            data.let { category ->
                DialogUtils.showDeleteConfirmation(
                    context = requireContext(),
                    onConfirm = {categoryViewModel.delete(category)},
                    title = "Delete category: ${data.displayName}",
                    message = "Do you wish to delete ${data.displayName} category?",
                )
            }
        }
    }

}