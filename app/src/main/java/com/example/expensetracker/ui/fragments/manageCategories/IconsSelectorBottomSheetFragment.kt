package com.example.expensetracker.ui.fragments.manageCategories

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.CategoryIconEnum
import com.example.expensetracker.ui.adapters.ExpenseAdapter
import com.example.expensetracker.ui.adapters.IconPickerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IconsSelectorBottomSheetFragment(private val onIconSelected: (CategoryIconEnum) -> Unit) :
    BottomSheetDialogFragment(R.layout.fragment_icon_selector_bottom_sheet) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycleView = view.findViewById<RecyclerView>(R.id.rvIcons)

        val iconsAdapter = IconPickerAdapter { selectedIcon ->
            onIconSelected(selectedIcon)
            dismiss()
        }
        recycleView.layoutManager = GridLayoutManager(requireContext(), 4)
        recycleView.adapter = iconsAdapter
        iconsAdapter.submitList(CategoryIconEnum.entries)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}