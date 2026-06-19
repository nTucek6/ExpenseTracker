package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.model.ManageCategories
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

        categoryAdapter = CategoryPagingAdapter { category -> showEditCategoryDialog(category) }
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            categoryViewModel.categoryPaging.collectLatest { pagingData ->
                categoryAdapter.submitData(pagingData)
            }
        }

        newCategoryBtn.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        lifecycleScope.launch {
            DialogUtils.showAddCategoryDialog(
                context = requireContext(),
                onAdd = { name ->
                    Log.d("CategoryAdd", name)
                    categoryViewModel.insert(displayName = name, null)
                },
            )
        }
    }

    private fun showEditCategoryDialog(category: ManageCategories) {
        lifecycleScope.launch {
            DialogUtils.showEditCategoryDialog(
                context = requireContext(),
                category = category,
                onEdit = { editedCategory ->
                    categoryViewModel.update(editedCategory)
                },
                onDelete = { id -> showDeleteCategoryDialog(id) }
            )
        }
    }

    private fun showDeleteCategoryDialog(
        categoryId: Int
    ) {
        lifecycleScope.launch {
            val data = categoryViewModel.findById(categoryId)
            data.let { category ->
                DialogUtils.showDeleteConfirmation(
                    context = requireContext(),
                    onConfirm = { categoryViewModel.delete(category) },
                    title = "Delete category: ${data.displayName}",
                    message = "Do you wish to delete ${data.displayName} category?",
                )
            }
        }
    }
}