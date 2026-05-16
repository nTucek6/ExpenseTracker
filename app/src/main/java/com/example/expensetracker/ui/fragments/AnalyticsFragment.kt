package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.R
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.utils.toDateString
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.getValue

class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    private lateinit var spendingChart: LineChart
    private lateinit var barData: LineData
    private lateinit var barDataSet: LineDataSet
    private lateinit var spendingData: ArrayList<Entry>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spendingChart = view.findViewById(R.id.lineGraph)
        setLineChart()
        if(spendingData.isNotEmpty()){
            barDataSet = LineDataSet(spendingData, "Data set")
            barData = LineData(barDataSet)
            spendingChart.data = barData
        }

    }

    private fun setLineChart() {

        spendingData = ArrayList()

        val labels = mutableListOf<String>()
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()

        val firstDayOfMonthMillis = today
            .withDayOfMonth(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        val lastDayOfMonthMillis = today
            .with(TemporalAdjusters.lastDayOfMonth())
            .atTime(LocalTime.MAX)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()

        viewLifecycleOwner.lifecycleScope.launch {
            val data =
                expenseViewModel.getDailyBudgetSpent(firstDayOfMonthMillis, lastDayOfMonthMillis)
            data.value?.forEachIndexed { index, item ->
                spendingData.add(Entry(index.toFloat(), item.amount.toFloat()))
                labels.add(item.date.toDateString())
            }
            spendingChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -45f
                setDrawGridLines(false)
            }
        }
    }

}