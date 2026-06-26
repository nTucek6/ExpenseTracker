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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.MonthEnum
import com.example.expensetracker.data.enums.PeriodChipEnum
import com.example.expensetracker.data.model.AnalyticsSummary
import com.example.expensetracker.data.viewModel.AnalyticsViewModel
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.data.viewModel.MonthlySummaryViewModel
import com.example.expensetracker.ui.adapters.TopCategoriesAdapter
import com.example.expensetracker.utils.ChartUtils
import com.example.expensetracker.utils.firstOfMonthCalendarToMillis
import com.example.expensetracker.utils.formatWeekDate
import com.example.expensetracker.utils.getLast3MonthsRange
import com.example.expensetracker.utils.getLast6MonthsRange
import com.example.expensetracker.utils.getLastYearRange
import com.example.expensetracker.utils.lastOfMonthCalendarToMillis
import com.example.expensetracker.utils.toDateString
import com.example.expensetracker.utils.toYearAndMonth
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
import com.google.android.material.button.MaterialButton
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

    private lateinit var tvCurrentDateRange: TextView
    private lateinit var spendingChart: LineChart
    private lateinit var categorySpendingChart: PieChart

    private lateinit var spendingData: ArrayList<Entry>
    private lateinit var categorySpendingData: ArrayList<PieEntry> //value, label su ulazne vrijednosti

    private lateinit var incTotalSpent: MaterialCardView
    private lateinit var incTotalIncome: MaterialCardView
    private lateinit var incBalance: MaterialCardView
    private lateinit var incAvgDay: MaterialCardView
    private lateinit var topCategoryAdapter: TopCategoriesAdapter
    private var monthStart = Calendar.getInstance().firstOfMonthCalendarToMillis()
    private var monthEnd = Calendar.getInstance().lastOfMonthCalendarToMillis()

    private var period: PeriodChipEnum = PeriodChipEnum.THIS_MONTH

    var valueColor: Int = 0
    var labelColor: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        valueColor = ContextCompat.getColor(requireContext(), R.color.chart_value_text)
        labelColor = ContextCompat.getColor(requireContext(), R.color.chart_label_text)

        val btnOpenFilter: MaterialButton = view.findViewById(R.id.btn_open_filter)

        tvCurrentDateRange = view.findViewById(R.id.tvMonthYear)
        incTotalSpent = view.findViewById(R.id.incTotalSpent)
        incTotalIncome = view.findViewById(R.id.incTotalIncome)
        incBalance = view.findViewById(R.id.incBalance)
        incAvgDay = view.findViewById(R.id.incAvgDay)

        incTotalSpent.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_spent)
        incTotalIncome.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.total_income)
        incBalance.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.balance)
        incAvgDay.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.avg_per_day)


        spendingChart = view.findViewById(R.id.lineGraph)
        categorySpendingChart = view.findViewById(R.id.pieGraph)

        topCategoryAdapter = TopCategoriesAdapter()
        val topCategoriesRecycleView: RecyclerView =
            view.findViewById(R.id.rv_top_categories)

        topCategoriesRecycleView.layoutManager = LinearLayoutManager(context)
        topCategoriesRecycleView.adapter = topCategoryAdapter

        setAnalytics()

        parentFragmentManager.setFragmentResultListener(
            AnalyticsFilterBottomSheetFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val periodId =
                bundle.getString(AnalyticsFilterBottomSheetFragment.KEY_PERIOD_ID) ?: "NULL"

            val selectedPeriod = PeriodChipEnum.fromText(requireContext(), periodId)

            val startDate =
                bundle.getLong(AnalyticsFilterBottomSheetFragment.KEY_DATE_START)
            val endDate = bundle.getLong(AnalyticsFilterBottomSheetFragment.KEY_DATE_END)
            period = selectedPeriod
            setRange(selectedPeriod, startDate, endDate)
        }

        parentFragmentManager.setFragmentResultListener(
            AnalyticsFilterBottomSheetFragment.REQUEST_KEY_CLOSED,
            viewLifecycleOwner
        ) { _, _ ->
            btnOpenFilter.isEnabled = true
        }

        btnOpenFilter.setOnClickListener {
            btnOpenFilter.isEnabled = false
            AnalyticsFilterBottomSheetFragment().show(parentFragmentManager, "AnalyticsFilter")
        }
    }

    private fun setSummary() {
        lifecycleScope.launch {
            val summary = analyticsViewModel.getSummary(
                monthStart,
                monthEnd
            )

            val (startYear, startMonth) = monthStart.toYearAndMonth()
            val (endYear, endMonth) = monthEnd.toYearAndMonth()

            val monthlySummary = monthlySummaryViewModel.getRangeMonthBudget(
                startYear,
                startMonth,
                endYear,
                endMonth
            ).asFlow().first()

            var balance = 0.0
            var money = 0.0

            for (m in monthlySummary) {
                money += m.money
                balance += m.money - m.spent
            }

            if(monthlySummary.isNotEmpty()){
                incTotalSpent.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), summary.totalSpent)
                incTotalIncome.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), money)
                incBalance.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), balance)
                incAvgDay.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), summary.avgPerDay)
            } else{
                incTotalSpent.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), 0.0)
                incTotalIncome.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), 0.0)
                incBalance.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), 0.0)
                incAvgDay.findViewById<TextView>(R.id.tvValue).text =
                    String.format(getString(R.string.price_format), 0.0)
            }

        }
    }

    private fun setLineChart() {
        spendingData = ArrayList()

        val labels = mutableListOf<String>()

        lifecycleScope.launch {

            when (period) {
                PeriodChipEnum.THIS_MONTH -> {
                    val data =
                        expenseViewModel.getDailyBudgetSpent(
                            monthStart,
                            monthEnd
                        )

                    data.forEachIndexed { index, item ->
                        spendingData.add(Entry(index.toFloat(), item.amount.toFloat()))
                        labels.add(item.date.toDateString())
                    }
                }

                PeriodChipEnum.THREE_MONTHS -> {
                    val data =
                        expenseViewModel.getWeeklyBudgetSpent(
                            monthStart,
                            monthEnd
                        )
                    data.forEachIndexed { index, item ->
                        spendingData.add(Entry(index.toFloat(), item.total.toFloat()))
                        labels.add(formatWeekDate(item.weekStartDate, item.weekEndDate))
                    }
                }

                PeriodChipEnum.SIX_MONTHS -> {
                    val data =
                        expenseViewModel.getWeeklyBudgetSpent(
                            monthStart,
                            monthEnd
                        )
                    data.forEachIndexed { index, item ->
                        spendingData.add(Entry(index.toFloat(), item.total.toFloat()))
                        labels.add(formatWeekDate(item.weekStartDate, item.weekEndDate))
                    }
                }

                PeriodChipEnum.YEAR -> {
                    val data =
                        expenseViewModel.getMonthlyBudgetSpent(
                            monthStart,
                            monthEnd
                        )
                    data.forEachIndexed { index, item ->
                        spendingData.add(Entry(index.toFloat(), item.total.toFloat()))
                        //labels.add(formatWeekDate(item.weekStartDate, item.weekEndDate))
                        labels.add("${item.year}-${item.month}")
                    }
                }

                PeriodChipEnum.CUSTOM -> {
                    val data =
                        expenseViewModel.getDailyBudgetSpent(
                            monthStart,
                            monthEnd
                        )

                    data.forEachIndexed { index, item ->
                        spendingData.add(Entry(index.toFloat(), item.amount.toFloat()))
                        labels.add(item.date.toDateString())
                    }
                }
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
            if (topCategorySpent.isNotEmpty()) {
                topCategoryAdapter.submitList(topCategorySpent)
            } else{
                topCategoryAdapter.submitList(listOf())
            }
        }
    }

    private fun setRange(periodEnum: PeriodChipEnum, startDate: Long?, endDate: Long?) {
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
                if (startDate != null && endDate != null) {
                    monthStart = startDate
                    monthEnd = endDate
                }
            }
        }
        setAnalytics()
    }
    private fun setAnalytics() {
        setDateText()
        setSummary()
        setLineChart()
        setPieChart()
        getTopCategorySpent()
    }
    private fun setDateText() {
        if (period == PeriodChipEnum.THIS_MONTH) {
            val (year, month) = monthStart.toYearAndMonth()
            tvCurrentDateRange.text = String.format(
                getString(R.string.dashboard_date_format),
                MonthEnum.fromNumber(month).displayName.let { requireContext().getString(it) },
                year.toString()
            )
        } else {
            tvCurrentDateRange.text = "${monthStart.toDateString()} - ${monthEnd.toDateString()}"
        }
    }
}