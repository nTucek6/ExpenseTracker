package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.expensetracker.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    private lateinit var barData: BarData
    private lateinit var barDataSet: BarDataSet
    private lateinit var data: ArrayList<BarEntry>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val graph: BarChart = view.findViewById(R.id.graph)

        setData()

        barDataSet = BarDataSet(data, "Data set")
        barData = BarData(barDataSet)

        graph.data = barData

    }

    private fun setData() {
        data = arrayListOf(
            BarEntry(1f, 2f),
            BarEntry(2f, 2f),
            BarEntry(3f, 2f)
        )
    }

}