package com.example.expensetracker.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.PeriodChipEnum
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AnalyticsFilterBottomSheetFragment :
    BottomSheetDialogFragment(R.layout.fragment_analytics_filter_bottom_sheet) {

    private lateinit var chipGroup: ChipGroup

    companion object {
        const val REQUEST_KEY = "analytics_filter_request"
        const val REQUEST_KEY_CLOSED = "filter_closed"
        const val KEY_PERIOD_ID = "period_id"
        const val KEY_PERIOD = "period"
        const val KEY_TYPE = "type"
    }
    private fun applyFilters() {

        val selectedChip = chipGroup.findViewById<Chip>(chipGroup.checkedChipId)
        val selectedPeriod = PeriodChipEnum.entries.firstOrNull { it == selectedChip.tag } ?: PeriodChipEnum.THIS_MONTH

        val result = bundleOf(
            KEY_PERIOD to selectedPeriod,
            KEY_PERIOD_ID to selectedChip.tag.toString()
            //KEY_TYPE to type
        )
        parentFragmentManager.setFragmentResult(REQUEST_KEY, result)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        parentFragmentManager.setFragmentResult(
            REQUEST_KEY_CLOSED,
            bundleOf()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDismiss: Button = view.findViewById(R.id.btn_dismiss)
        val btnApply: Button = view.findViewById(R.id.btn_apply)

        chipGroup = view.findViewById(R.id.chipGroupPeriod)


        PeriodChipEnum.entries.forEach { it ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = getString(it.displayName)
                isClickable = true
                isCheckable = true
                tag = it
            }
            chipGroup.addView(chip) }

        btnDismiss.setOnClickListener {
            dismiss()
        }
        btnApply.setOnClickListener {
            applyFilters()
            dismiss()
        }
    }

}