package com.example.expensetracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.databinding.ItemCategoryBinding

class CategoryPagingAdapter(
    private val onTrashClick: (Categories) -> Unit
) : PagingDataAdapter<Categories, CategoryPagingAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Categories>() {
        override fun areItemsTheSame(old: Categories, new: Categories) = old.id == new.id
        override fun areContentsTheSame(old: Categories, new: Categories) = old == new
    }) {

    class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemCategoryBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val category = getItem(position)

        if (category != null) {
            viewHolder.binding.tvCategory.text = category.displayName

            viewHolder.binding.ivRemoveCategory.setOnClickListener {
                onTrashClick(category)
            }

        }

    }


}