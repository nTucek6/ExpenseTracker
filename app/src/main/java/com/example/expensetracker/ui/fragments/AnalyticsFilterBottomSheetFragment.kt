package com.example.expensetracker.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.PeriodChipEnum
import com.example.expensetracker.utils.toDateTimeString
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker

class AnalyticsFilterBottomSheetFragment :
    BottomSheetDialogFragment(R.layout.fragment_analytics_filter_bottom_sheet) {

    private lateinit var chipGroup: ChipGroup

    private var startDate: Long = 0
    private var endDate: Long = 0

    companion object {
        const val REQUEST_KEY = "analytics_filter_request"
        const val REQUEST_KEY_CLOSED = "filter_closed"
        const val KEY_PERIOD_ID = "period_id"
        const val KEY_PERIOD = "period"
        const val KEY_DATE_START = "date_start"
        const val KEY_DATE_END = "date_end"
    }

    private fun applyFilters() {

        val selectedChip = chipGroup.findViewById<Chip>(chipGroup.checkedChipId)
        val selectedPeriod = PeriodChipEnum.entries.firstOrNull { it == selectedChip.tag }
            ?: PeriodChipEnum.THIS_MONTH

        val result = bundleOf(
            KEY_PERIOD to selectedPeriod,
            KEY_PERIOD_ID to selectedChip.tag.toString(),
            KEY_DATE_START to startDate,
            KEY_DATE_END to endDate,
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
            chipGroup.addView(chip)
        }


        /* chipGroup.setOnCheckedStateChangeListener { group, chip ->
             val selectedChip = chipGroup.findViewById<Chip>(chipGroup.checkedChipId)
             val selectedPeriod = PeriodChipEnum.entries.firstOrNull { it == selectedChip.tag } ?: PeriodChipEnum.THIS_MONTH

             if(selectedPeriod == PeriodChipEnum.CUSTOM){
                 showDatePickerDialog()
             }

         }*/

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = group.checkedChipId
            if (checkedId == View.NO_ID) return@setOnCheckedStateChangeListener

            val selectedChip =
                group.findViewById<Chip>(checkedId) ?: return@setOnCheckedStateChangeListener
            val selectedPeriod = selectedChip.tag as? PeriodChipEnum ?: PeriodChipEnum.THIS_MONTH

            if (selectedPeriod == PeriodChipEnum.CUSTOM) {
                showDatePickerIfNeeded()
            }
        }

        btnDismiss.setOnClickListener {
            dismiss()
        }
        btnApply.setOnClickListener {
            applyFilters()
            dismiss()
        }
    }

    private fun showDatePickerIfNeeded() {
        val tag = "DATE_PICKER"

        if (parentFragmentManager.findFragmentByTag(tag) != null) return

        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select range")
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val start = selection.first
            val end = selection.second

            startDate = start
            endDate = end

            if (start != null && end != null) {
                Log.d(
                    "CustomDatePickerData",
                    "${start.toDateTimeString("dd.MM.yyyy")} - ${end.toDateTimeString("dd.MM.yyyy")}"
                )
            }
        }
        picker.show(parentFragmentManager, tag)
    }
}