package com.example.expensetracker.ui.fragments.manageCategories

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
import com.example.expensetracker.utils.toDateTimeString
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageCategoriesFragment : Fragment(R.layout.fragment_manage_categories) {

    private val categoryViewModel: CategoriesViewModel by activityViewModels()

    private lateinit var categoryAdapter: CategoryPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newCategoryBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.newCategoryBtn)

        categoryAdapter = CategoryPagingAdapter { category ->
            //Log.d("TimeUpdated", category.updatedAt.toDateTimeString())
            ManageCategoriesBottomSheetFragment(
                category,
                onCategorySave = null
            ) { category ->
                Log.d("AddEditCategory", category.toString())
                categoryViewModel.update(category)
            }.show(parentFragmentManager, "EditCategory")
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_categories)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            categoryViewModel.categoryPaging.collectLatest { pagingData ->
                categoryAdapter.submitData(pagingData)
            }
        }
        newCategoryBtn.setOnClickListener {
            ManageCategoriesBottomSheetFragment(null, { name, category ->
                categoryViewModel.insert(displayName = name, category)
            }, onCategoryEdit = null).show(parentFragmentManager, "AddCategory")
        }
    }
}