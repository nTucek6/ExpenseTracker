package com.example.expensetracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.viewModel.ExpenseViewModel
import com.example.expensetracker.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.exp

class ExpenseAdapter : ListAdapter<Expense, ExpenseAdapter.ViewHolder>(
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
        viewHolder.binding.tvCategory.text = expense.category.getDisplayName()
        viewHolder.binding.tvDate.text = expense.createdAt.toDateString()
    }

}

fun Long.toDateString(pattern: String = "dd.MM.YYYY."): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}