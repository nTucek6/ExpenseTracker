package com.example.expensetracker.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.model.TopCategorySpent
import com.example.expensetracker.databinding.ItemAnalyticsCategoryCardBinding

class TopCategoriesAdapter : ListAdapter<TopCategorySpent, TopCategoriesAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<TopCategorySpent>() {
        override fun areItemsTheSame(old: TopCategorySpent, new: TopCategorySpent) =
            old.categoryId == new.categoryId

        override fun areContentsTheSame(old: TopCategorySpent, new: TopCategorySpent) = old == new
    }) {
    class ViewHolder(val binding: ItemAnalyticsCategoryCardBinding) :
        RecyclerView.ViewHolder(binding.root)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = ItemAnalyticsCategoryCardBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val category = getItem(position)

        val rbr = position + 1

        viewHolder.binding.tvNumber.text = "$rbr."
        viewHolder.binding.tvCategory.text = category.categoryName
        viewHolder.binding.tvSpent.text =
            String.format(
                viewHolder.itemView.context.getString(R.string.price_format),
                category.totalSpent
            )
    }
}