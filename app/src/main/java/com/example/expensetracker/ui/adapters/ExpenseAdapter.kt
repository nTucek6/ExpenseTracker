package com.example.expensetracker.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LOG_TAG
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Expense
import com.example.expensetracker.data.model.ExpenseWithCategory
import com.example.expensetracker.databinding.ItemExpenseBinding
//import com.example.expensetracker.databinding.ItemExpenseBinding
import com.example.expensetracker.utils.isToday
import com.example.expensetracker.utils.toDateString
import com.example.expensetracker.utils.toTimeString
import kotlin.math.exp

class ExpenseAdapter(private val onItemClick: (Expense, String) -> Unit) :
    ListAdapter<ExpenseWithCategory, ExpenseAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<ExpenseWithCategory>() {
            override fun areItemsTheSame(old: ExpenseWithCategory, new: ExpenseWithCategory) = old.id == new.id
            override fun areContentsTheSame(old: ExpenseWithCategory, new: ExpenseWithCategory) = old == new
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
        if(expense.imageSvg != null)
            viewHolder.binding.ivCategory.setImageDrawable(viewHolder.itemView.context.getDrawable(expense.imageSvg))
        viewHolder.binding.tvCategory.text = expense.categoryName

        if (expense.createdAt.isToday()) {
            viewHolder.binding.tvDate.text = expense.createdAt.toTimeString()
        } else {
            viewHolder.binding.tvDate.text = expense.createdAt.toDateString()
        }

        val e = Expense(
            id = expense.id,
            amount = expense.amount,
            categoryId = expense.categoryId,
            description = expense.description,
            createdAt = expense.createdAt
        )
        viewHolder.binding.cardView.setOnClickListener { onItemClick(e, expense.categoryName) }
    }
}