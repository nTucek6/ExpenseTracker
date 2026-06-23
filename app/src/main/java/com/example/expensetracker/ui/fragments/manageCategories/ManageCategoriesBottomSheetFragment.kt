package com.example.expensetracker.ui.fragments.manageCategories

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.expensetracker.R
import com.example.expensetracker.data.enums.CategoryIconEnum
import com.example.expensetracker.data.model.ManageCategories
import com.example.expensetracker.data.viewModel.CategoriesViewModel
import com.example.expensetracker.utils.DialogUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import kotlin.getValue

class ManageCategoriesBottomSheetFragment(
    private val category: ManageCategories?,
    private val onCategorySave: ((String, CategoryIconEnum) -> Unit?)?,
    private val onCategoryEdit: ((ManageCategories) -> Unit?)?,
) :
    BottomSheetDialogFragment(R.layout.fragment_manage_categories_bottom_sheet) {

    private val categoryViewModel: CategoriesViewModel by activityViewModels()

    private lateinit var imagePreview: ImageView
    private lateinit var imagePreviewText: TextView

    private lateinit var icon: CategoryIconEnum

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val tilCategoryName = view.findViewById<TextInputLayout>(R.id.tilCategoryName)
        val openIconSelector = view.findViewById<MaterialCardView>(R.id.mcv_cardPickIcon)
        imagePreview = view.findViewById(R.id.ivSelectedIcon)
        imagePreviewText = view.findViewById(R.id.tvIconSubtitle)
        val saveCategoryBtn = view.findViewById<MaterialButton>(R.id.btnSaveCategory)
        val deleteCategoryBtn = view.findViewById<MaterialButton>(R.id.btnDeleteCategory)

        icon = CategoryIconEnum.OTHER


        if (category != null) {
            tilCategoryName.editText?.setText(category.displayName)
            setIconPreview(category.image)
            deleteCategoryBtn.visibility = View.VISIBLE
        } else {
            setIconPreview(CategoryIconEnum.OTHER)
        }

        openIconSelector.setOnClickListener {
            val sheet = IconsSelectorBottomSheetFragment { selectedIcon ->
                setIconPreview(selectedIcon)
            }
            sheet.show(parentFragmentManager, "IconsSelectorBottomSheet")
        }

        saveCategoryBtn.setOnClickListener {
            val categoryInput = tilCategoryName.editText?.text.toString()
            if (category != null) {
                val editedCategory = ManageCategories(
                    id = category.id,
                    displayName = categoryInput,
                    isDefault = category.isDefault,
                    image = icon,
                    expensesCount = category.expensesCount,
                )
                onCategoryEdit?.invoke(editedCategory)
            } else {
                onCategorySave?.invoke(categoryInput, icon)
            }
            dismiss()
        }

        if (category != null) {
            deleteCategoryBtn.setOnClickListener {
                if (category.expensesCount > 0)
                    DialogUtils.showDeleteInfoDialog(
                        requireContext(),
                        String.format(
                            getString(R.string.no_delete_category_title),
                            category.displayName
                        ),
                        getString(R.string.no_delete_category_message)
                    )
                else showDeleteCategoryDialog(category.id)
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    private fun setIconPreview(selectedIcon: CategoryIconEnum) {
        icon = selectedIcon
        imagePreview.setImageResource(selectedIcon.resId)
        imagePreviewText.text = selectedIcon.key
    }


    private fun showDeleteCategoryDialog(
        categoryId: String
    ) {
        lifecycleScope.launch {
            val data = categoryViewModel.findById(categoryId)
            data.let { category ->
                DialogUtils.showDeleteConfirmation(
                    context = requireContext(),
                    onConfirm = { categoryViewModel.delete(category) },
                    title = String.format(
                        getString(R.string.delete_category_title),
                        data.displayName
                    ),//"Delete category: ${data.displayName}",
                    message = String.format(
                        getString(R.string.delete_category_message),
                        data.displayName
                    ) //"Do you wish to delete ${data.displayName} category?",
                )
            }
        }
    }
}