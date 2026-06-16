package com.example.expensetracker.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.PeriodChipEnum
import com.example.expensetracker.data.model.AnalyticsSummary
import com.example.expensetracker.data.viewModel.AnalyticsViewModel
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.utils.ChartUtils
import com.example.expensetracker.utils.firstOfMonthCalendarToMillis
import com.example.expensetracker.utils.getLast3MonthsRange
import com.example.expensetracker.utils.getLast6MonthsRange
import com.example.expensetracker.utils.getLastYearRange
import com.example.expensetracker.utils.lastOfMonthCalendarToMillis
import com.example.expensetracker.utils.toDateString
import com.example.expensetracker.utils.todayCalendarToMillis
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import kotlin.String
import kotlin.getValue

class AnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()

    private val monthlySummaryViewModel: MonthlySummaryViewModel by activityViewModels()

    private lateinit var spendingChart: LineChart
    private lateinit var categorySpendingChart: PieChart

    //private lateinit var barData: LineData
    //private lateinit var barDataSet: LineDataSet
    private lateinit var spendingData: ArrayList<Entry>
    private lateinit var categorySpendingData: ArrayList<PieEntry> //value, label su ulazne vrijednosti

    private lateinit var incTotalSpent: MaterialCardView
    private lateinit var incTotalIncome: MaterialCardView
    private lateinit var incBalance: MaterialCardView
    private lateinit var incAvgDay: MaterialCardView

    private lateinit var incCategory1: MaterialCardView
    private lateinit var incCategory2: MaterialCardView
    private lateinit var incCategory3: MaterialCardView

    private var monthStart = Calendar.getInstance().firstOfMonthCalendarToMillis()
    private var monthEnd = Calendar.getInstance().lastOfMonthCalendarToMillis()

    var valueColor: Int = 0
    var labelColor: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        valueColor = ContextCompat.getColor(requireContext(), R.color.chart_value_text)
        labelColor = ContextCompat.getColor(requireContext(), R.color.chart_label_text)

        val btnOpenFilter: Button = view.findViewById(R.id.btn_open_filter)

        incTotalSpent = view.findViewById(R.id.incTotalSpent)
        incTotalIncome = view.findViewById(R.id.incTotalIncome)
        incBalance = view.findViewById(R.id.incBalance)
        incAvgDay = view.findViewById(R.id.incAvgDay)

        incCategory1 = view.findViewById(R.id.inc_category_1)
        incCategory2 = view.findViewById(R.id.inc_category_2)
        incCategory3 = view.findViewById(R.id.inc_category_3)

        incTotalSpent.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_spent)
        incTotalIncome.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_income)
        incBalance.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.balance)
        incAvgDay.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.avg_per_day)


        spendingChart = view.findViewById(R.id.lineGraph)
        categorySpendingChart = view.findViewById(R.id.pieGraph)

        setAnalytics()


        parentFragmentManager.setFragmentResultListener(
            AnalyticsFilterBottomSheetFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            //val period = bundle.getString(AnalyticsFilterBottomSheetFragment.KEY_PERIOD) ?: "This Month"
            val periodId =
                bundle.getString(AnalyticsFilterBottomSheetFragment.KEY_PERIOD_ID) ?: "NULL"

            val selectedPeriod = PeriodChipEnum.fromText(requireContext(), periodId)

            // update analytics
            //loadAnalytics(period, type)
            setRange(selectedPeriod)
        }
        btnOpenFilter.setOnClickListener {
            AnalyticsFilterBottomSheetFragment().show(parentFragmentManager, "AnalyticsFilter")
        }


    }

    private fun setSummary() {
        lifecycleScope.launch {
            val summary = analyticsViewModel.getSummary(
                monthStart,
                monthEnd
            )

            val monthlySummary = monthlySummaryViewModel
                .getCurrentMonthBudget
                .asFlow()
                .first()

            val balance = monthlySummary.money - monthlySummary.spent

            Log.d("AnalyticsData", summary.toString())

            if (summary.totalSpent != 0.0) {
                incTotalSpent.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), summary.totalSpent)
                incTotalIncome.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), monthlySummary.money)
                incBalance.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), balance)
                incAvgDay.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), summary.avgPerDay)
            }

        }
    }

    private fun setLineChart() {
        spendingData = ArrayList()

        val labels = mutableListOf<String>()

        lifecycleScope.launch {
            val data =
                expenseViewModel.getDailyBudgetSpent(
                    monthStart,
                    monthEnd
                )
            data.forEachIndexed { index, item ->
                spendingData.add(Entry(index.toFloat(), item.amount.toFloat()))
                labels.add(item.date.toDateString())
            }

            if (spendingData.isNotEmpty()) {
                val lineDataSet = ChartUtils.setLineChartDataSet(requireContext(), spendingData)
                val lineData = LineData(lineDataSet)

                spendingChart.data = lineData
                spendingChart.xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(labels)
                    labelRotationAngle = -45f
                    textColor = labelColor
                    setDrawGridLines(false)
                }

                spendingChart.axisLeft.textColor = labelColor
                spendingChart.axisRight.textColor = labelColor
                spendingChart.legend.textColor = labelColor
                spendingChart.description.textColor = labelColor

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
            categorySpendingData.clear()

            val data =
                expenseViewModel.getSpentPerCategory(
                    monthStart,
                    monthEnd
                )

            categorySpendingData = ChartUtils.buildPieEntriesDynamic(requireContext(), data)

            if (categorySpendingData.isNotEmpty()) {

                categorySpendingChart.setUsePercentValues(true)
                val pieDataSet = ChartUtils.setPieDataSet(requireContext(), categorySpendingData)
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

    private fun getTopCategorySpent() {
        lifecycleScope.launch {
            val topCategorySpent = analyticsViewModel.getTopCategorySpent(
                monthStart,
                monthEnd
            )

            val s1 = topCategorySpent[0]
            val s2 = topCategorySpent[1]
            val s3 = topCategorySpent[2]

            setUpCategory(incCategory1, 1, s1.categoryName, s1.totalSpent)
            setUpCategory(incCategory2, 2, s2.categoryName, s2.totalSpent)
            setUpCategory(incCategory3, 3, s3.categoryName, s3.totalSpent)

            Log.d("AnalyticsData", topCategorySpent.toString())
        }

    }

    private fun setUpCategory(
        categoryView: MaterialCardView,
        number: Int,
        category: String,
        spent: Double
    ) {
        categoryView.findViewById<TextView>(R.id.tv_number).text = number.toString() + "."
        categoryView.findViewById<TextView>(R.id.tv_category).text = category
        categoryView.findViewById<TextView>(R.id.tv_spent).text =
            String.format(getString(R.string.price_format), spent)
    }


    private fun setRange(periodEnum: PeriodChipEnum) {
        when (periodEnum) {
            PeriodChipEnum.THIS_MONTH -> {
                monthStart = Calendar.getInstance().firstOfMonthCalendarToMillis()
                monthEnd = Calendar.getInstance().lastOfMonthCalendarToMillis()
            }

            PeriodChipEnum.THREE_MONTHS -> {
                val (startMillis, endMillis) = Calendar.getInstance().getLast3MonthsRange()
                monthStart = startMillis
                monthEnd = endMillis
            }

            PeriodChipEnum.SIX_MONTHS -> {
                val (startMillis, endMillis) = Calendar.getInstance().getLast6MonthsRange()
                monthStart = startMillis
                monthEnd = endMillis
            }

            PeriodChipEnum.YEAR -> {
                val (startMillis, endMillis) = Calendar.getInstance().getLastYearRange()
                monthStart = startMillis
                monthEnd = endMillis
            }
            PeriodChipEnum.CUSTOM -> {
                // show date picker
            }
        }
        setAnalytics()
    }


    private fun setAnalytics() {
        setSummary()
        setLineChart()
        setPieChart()
        getTopCategorySpent()
    }

}