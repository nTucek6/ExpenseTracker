package com.example.expensetracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(private val onItemClick: (Expense) -> Unit) : ListAdapter<Expense, ExpenseAdapter.ViewHolder>(
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

        viewHolder.binding.tvAmount.text = expense.amount.toString() + "€"
        //viewHolder.binding.tvAmount.text = String.format(requireContext().getString(R.string.price_format), yourDoubleValue)
        viewHolder.binding.tvCategory.text = expense.category.displayName
        viewHolder.binding.tvDate.text = expense.createdAt.toDateString()

        viewHolder.binding.cardView.setOnClickListener { onItemClick(expense) }
    }

}

fun Long.toDateString(pattern: String = "dd.MM.yyyy."): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}