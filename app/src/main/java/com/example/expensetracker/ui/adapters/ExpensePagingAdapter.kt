package com.example.expensetracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.databinding.ItemExpenseBinding
import com.example.expensetracker.utils.toDateString

class ExpensePagingAdapter(
    private val onItemClick: (Expense) -> Unit
) : PagingDataAdapter<Expense, ExpensePagingAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(old: Expense, new: Expense) = old.id == new.id
        override fun areContentsTheSame(old: Expense, new: Expense) = old == new
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

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val expense = getItem(position)
        if (expense != null) {
            viewHolder.binding.tvAmount.text = String.format(
                viewHolder.itemView.context.getString(R.string.price_format),
                expense.amount
            )

            viewHolder.binding.tvCategory.text = expense.category.displayName
            viewHolder.binding.tvDate.text = expense.createdAt.toDateString()
            viewHolder.binding.cardView.setOnClickListener { onItemClick(expense) }
        }
    }
}
