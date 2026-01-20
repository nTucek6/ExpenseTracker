package com.example.expensetracker.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import com.example.expensetracker.databinding.ItemExpenseBinding
import com.example.expensetracker.utils.toDateString
import com.example.expensetracker.utils.toLocalDate
import com.example.expensetracker.utils.toTimeString

class ExpensePagingAdapter(
    private val onItemClick: (ExpenseWithGroupSum) -> Unit
) : PagingDataAdapter<ExpenseWithGroupSum, ExpensePagingAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ExpenseWithGroupSum>() {
        override fun areItemsTheSame(old: ExpenseWithGroupSum, new: ExpenseWithGroupSum) = old.id == new.id
        override fun areContentsTheSame(old: ExpenseWithGroupSum, new: ExpenseWithGroupSum) = old == new
    }) {



    class ViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = ItemExpenseBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val expense = getItem(position)
        if (expense != null) {
            val createdAt = expense.createdAt
            val tvDisplayGroup = viewHolder.binding.tvDisplayGroup
            val tvDateDisplay = viewHolder.binding.tvDateDisplay
            val tvSpentDisplay = viewHolder.binding.tvSpentDisplay

            val showHeader = position == 0 ||
                    !isSameDay(getItem(position - 1)?.createdAt ?: 0, createdAt)

            tvDisplayGroup.isVisible = showHeader
            if (showHeader) {
                tvDateDisplay.text = expense.createdAt.toDateString()
                tvSpentDisplay.text = String.format(
                    viewHolder.itemView.context.getString(R.string.spent_info_format),
                    expense.dailySum
                )
            }

            viewHolder.binding.tvAmount.text = String.format(
                viewHolder.itemView.context.getString(R.string.price_format),
                expense.amount
            )
            viewHolder.binding.ivCategory.setImageDrawable(viewHolder.itemView.context.getDrawable(expense.category.imageSvg))
            viewHolder.binding.tvCategory.text = expense.category.displayName
            viewHolder.binding.tvDate.text = expense.createdAt.toTimeString()
            viewHolder.binding.cardView.setOnClickListener { onItemClick(expense) }
        }
    }

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val d1 = date1.toLocalDate()
        val d2 = date2.toLocalDate()
        return d1.year == d2.year &&
                d1.month == d2.month &&
                d1.dayOfMonth == d2.dayOfMonth
    }
}
