package com.example.expensetracker.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data.enums.CategoryIconEnum
import com.example.expensetracker.databinding.ItemCategoryIconBinding


class IconPickerAdapter(
    private val onClick: (CategoryIconEnum) -> Unit
) : ListAdapter<CategoryIconEnum, IconPickerAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<CategoryIconEnum>() {
        override fun areItemsTheSame(old: CategoryIconEnum, new: CategoryIconEnum) =
            old.resId == new.resId

        override fun areContentsTheSame(old: CategoryIconEnum, new: CategoryIconEnum) =
            old == new
    }) {

    class ViewHolder(val binding: ItemCategoryIconBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemCategoryIconBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val category = getItem(position)
        holder.binding.ivIcon.setImageDrawable(holder.itemView.context.getDrawable(category.resId))
        holder.binding.tvIconLabel.text =  holder.itemView.context.getString(category.displayName)

        holder.binding.root.setOnClickListener { onClick(category) }
    }
}