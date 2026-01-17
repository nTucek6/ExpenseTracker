package com.example.expensetracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.model.BudgetWithSpent
import com.example.expensetracker.databinding.ItemSummaryBinding

class SummaryPagingAdapter : PagingDataAdapter<BudgetWithSpent, SummaryPagingAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<BudgetWithSpent>() {
        override fun areItemsTheSame(old: BudgetWithSpent, new: BudgetWithSpent) =
            old.year == new.year && old.month == new.month

        override fun areContentsTheSame(old: BudgetWithSpent, new: BudgetWithSpent) = old == new
    }) {

    class ViewHolder(val binding: ItemSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemSummaryBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val summary = getItem(position)
        if (summary != null) {
            viewHolder.binding.tvDate.text = String.format(
                viewHolder.itemView.context.getString(R.string.date_time_info_format),
                summary.year.toString() + "." + summary.month.toString()
            )
            viewHolder.binding.tvMoney.text = String.format(
                viewHolder.itemView.context.getString(R.string.limit_info_format),
                summary.money
            )
            viewHolder.binding.tvSpent.text = String.format(
                viewHolder.itemView.context.getString(R.string.spent_info_format),
                summary.spent
            )
            viewHolder.binding.tvRemaining.text = String.format(
                viewHolder.itemView.context.getString(R.string.remaining_info_format),
                (summary.money - summary.spent)
            )
        }
    }
}