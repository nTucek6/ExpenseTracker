package com.example.expensetracker.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.databinding.ItemExpenseBinding
import com.example.expensetracker.utils.isToday
import com.example.expensetracker.utils.toDateString
import com.example.expensetracker.utils.toTimeString

class ExpenseAdapter(private val onItemClick: (Expense) -> Unit) :
    ListAdapter<Expense, ExpenseAdapter.ViewHolder>(
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val expense = getItem(position)

        viewHolder.binding.tvAmount.text = String.format(
            viewHolder.itemView.context.getString(R.string.price_format),
            expense.amount
        )
        viewHolder.binding.ivCategory.setImageDrawable(viewHolder.itemView.context.getDrawable(expense.category.imageSvg))
        viewHolder.binding.tvCategory.text = expense.category.displayName

        if (expense.createdAt.isToday()) {
            viewHolder.binding.tvDate.text = expense.createdAt.toTimeString()
        } else {
            viewHolder.binding.tvDate.text = expense.createdAt.toDateString()
        }

        viewHolder.binding.cardView.setOnClickListener { onItemClick(expense) }
    }
}