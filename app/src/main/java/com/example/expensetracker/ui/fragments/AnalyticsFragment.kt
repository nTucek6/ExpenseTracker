package com.example.expensetracker.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.R
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.utils.toDateString
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.getValue

class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    private lateinit var spendingChart: LineChart
    private lateinit var categorySpendingChart: PieChart

    //private lateinit var barData: LineData
    //private lateinit var barDataSet: LineDataSet
    private lateinit var spendingData: ArrayList<Entry>
    private lateinit var categorySpendingData: ArrayList<PieEntry> //value, label su ulazne vrijednosti

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val incTotalSpent: MaterialCardView = view.findViewById(R.id.incTotalSpent)
        val incTotalIncome: MaterialCardView = view.findViewById(R.id.incTotalIncome)
        val incBalance: MaterialCardView = view.findViewById(R.id.incBalance)
        val incAvgDay: MaterialCardView = view.findViewById(R.id.incAvgDay)

        incTotalSpent.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_spent)
        incTotalIncome.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_spent)
        incBalance.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_spent)
        incAvgDay.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_spent)

        spendingChart = view.findViewById(R.id.lineGraph)
        categorySpendingChart = view.findViewById(R.id.pieGraph)
        setLineChart()
        setPieChart()
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

        lifecycleScope.launch {
            val data =
                expenseViewModel.getDailyBudgetSpent(firstDayOfMonthMillis, lastDayOfMonthMillis)
            data.forEachIndexed { index, item ->
                spendingData.add(Entry(index.toFloat(), item.amount.toFloat()))
                labels.add(item.date.toDateString())
            }

            if (spendingData.isNotEmpty()) {
                val decimalFormat = DecimalFormat("0.00")
                val lineDataSet = LineDataSet(spendingData, "Data set").apply {
                    setDrawValues(true)
                    valueTextSize = 10f
                    valueTextColor = Color.WHITE
                    valueFormatter = object : ValueFormatter() {
                        override fun getPointLabel(entry: Entry?): String {
                            return if (entry == null) "" else decimalFormat.format(entry.y)
                        }
                    }
                    setDrawCircles(true)
                    setCircleColor(Color.WHITE)
                }
                val lineData = LineData(lineDataSet)

                spendingChart.data = lineData
                spendingChart.xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(labels)
                    labelRotationAngle = -45f
                    setDrawGridLines(false)
                }

                spendingChart.notifyDataSetChanged()
                spendingChart.invalidate()
            } else {
                spendingChart.clear()
                spendingChart.setNoDataText("No spending data for this period")
                spendingChart.invalidate()
            }

        }
    }

    private fun setPieChart() {

        categorySpendingData = ArrayList()

        lifecycleScope.launch {
            val data =
                expenseViewModel.getSpentPerCategory(dateFrom = null, dateTo = null)

            val sumCategoryValues = data.sumOf { it.amount }
            data.forEachIndexed { index, item ->
                val percent = (item.amount / sumCategoryValues) * 100
                categorySpendingData.add(PieEntry(percent.toFloat(), item.category))
            }
            if (categorySpendingData.isNotEmpty()) {

                val valueColor = ContextCompat.getColor(requireContext(), R.color.chart_value_text)
                val labelColor = ContextCompat.getColor(requireContext(), R.color.chart_label_text)

                val pieDataSet = PieDataSet(categorySpendingData, "Data set").apply {
                    valueTextSize = 20f
                    valueTextColor = valueColor
                    valueFormatter = PercentFormatter(categorySpendingChart)
                }
                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
                val pieData = PieData(pieDataSet)
                pieData.setValueTextColor(valueColor)

                categorySpendingChart.data = pieData
                categorySpendingChart.setEntryLabelColor(labelColor)
                categorySpendingChart.legend.textColor = labelColor

                categorySpendingChart.notifyDataSetChanged()
                categorySpendingChart.invalidate()
            } else {
                categorySpendingChart.clear()
                categorySpendingChart.setNoDataText("No spending data for this period")
                categorySpendingChart.invalidate()
            }
        }
    }
}