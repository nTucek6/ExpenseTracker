package com.example.expensetracker.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.ExpenseEnum
import com.example.expensetracker.data.model.SpentPerCategory
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat

object ChartUtils {

    fun setLineChartDataSet(context: Context, data: ArrayList<Entry>): LineDataSet{
        val decimalFormat = DecimalFormat("0.00")
        return LineDataSet(data, "Data set").apply {
            setDrawValues(true)
            valueTextSize = 10f
            valueTextColor = ContextCompat.getColor(context, R.color.chart_value_text)
            valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return if (entry == null) "" else decimalFormat.format(entry.y)
                }
            }
            setDrawCircles(true)
            setCircleColor(ContextCompat.getColor(context, R.color.chart_label_text))
        }
    }


    fun buildPieEntriesDynamic(
        context: Context,
        data: List<SpentPerCategory>
    ): ArrayList<PieEntry> {

        val minPercent: Double = 3.5
        val maxSlices: Int = 5
        val otherLabel: String = context.getString(ExpenseEnum.OTHER.displayName)

        val total = data.sumOf { it.amount }
        if (total == 0.0) return ArrayList()

        val sorted = data.sortedByDescending { it.amount }

        val result = ArrayList<PieEntry>()
        var otherSum = 0.0

        sorted.forEachIndexed { index, item ->
            val percent = (item.amount / total) * 100.0
            val shouldGroup = percent < minPercent || index >= maxSlices

            if (shouldGroup) {
                otherSum += item.amount
            } else {
                result.add(PieEntry(item.amount.toFloat(), item.category))
            }
        }
        if (otherSum > 0.0) {
            val index = result.indexOfFirst { it.label == otherLabel }
            if (index != -1) {
                val old = result[index]
                result[index] = PieEntry(old.value + otherSum.toFloat(), old.label)
            } else {
                result.add(PieEntry(otherSum.toFloat(), otherLabel))
            }
        }
        return result
    }

    fun setPieDataSet(context: Context,data: ArrayList<PieEntry>): PieDataSet{
        val pieDataSet = PieDataSet(data, "Data set").apply {
            valueTextSize = 20f
            valueTextColor = ContextCompat.getColor(context, R.color.chart_value_text)
            valueFormatter = object : PercentFormatter() {
                override fun getPieLabel(value: Float, pieEntry: PieEntry?): String? {
                    return if (value < 3.5f) "" else super.getPieLabel(value, pieEntry)
                }
            }
        }
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        return pieDataSet
    }

}