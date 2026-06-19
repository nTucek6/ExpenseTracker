package com.example.expensetracker.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.model.ManageCategories
import com.example.expensetracker.databinding.ItemCategoryBinding

class CategoryPagingAdapter(
    private val onCategoryClick: (ManageCategories) -> Unit
) : PagingDataAdapter<ManageCategories, CategoryPagingAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ManageCategories>() {
        override fun areItemsTheSame(old: ManageCategories, new: ManageCategories) =
            old.id == new.id

        override fun areContentsTheSame(old: ManageCategories, new: ManageCategories) = old == new
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val category = getItem(position)

        if (category != null) {
            viewHolder.binding.tvCategory.text = category.displayName

            if (category.imageSvg != 0) {
                viewHolder.binding.ivIcon.setImageDrawable(
                    viewHolder.itemView.context.getDrawable(
                        category.imageSvg
                    )
                )
            }
            viewHolder.binding.tvIsUsedNumeric.text = String.format(
                viewHolder.binding.root.context.getString(R.string.used_categories),
                category.expensesCount.toString()
            )

            if (!category.isDefault) {
                viewHolder.binding.tvIsDefault.text =
                    viewHolder.binding.root.context.getString(R.string.custom)
                viewHolder.binding.mcvCategory.setOnClickListener {
                    onCategoryClick(category)
                }
            } else {
                viewHolder.binding.tvIsDefault.text =
                    viewHolder.binding.root.context.getString(R.string.default_category)
            }
        }
    }
}