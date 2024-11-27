package com.wuc.ft_home.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.DialogCompanyBinding
import com.wuc.ft_home.databinding.ItemStringBinding
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter

/**
 * @author: wuc
 * @date: 2024/11/26
 * @description:
 */
class CompanyDialog(
    private val companyMap: Map<String, Int>,
    private var selectedTabPosition: Int
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogCompanyBinding
    private lateinit var mCompanyAdapter: CompanyAdapter

    /**
     * setStyle 圆角效果
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogCompanyBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivCancel.setOnClickListener { dismiss() }

        mCompanyAdapter = CompanyAdapter(requireContext())
        mCompanyAdapter.setData(companyMap.keys.toMutableList())
        binding.recycleView.adapter = mCompanyAdapter
        // 设置默认选中项
        if (selectedTabPosition >= 0 && selectedTabPosition < companyMap.size) {
            mCompanyAdapter.updateSelectedPosition(selectedTabPosition)
        }
        mCompanyAdapter.onItemClickListener = { view, position ->
            if (selectedTabPosition != position) {
                selectedTabPosition = position
                mCompanyAdapter.updateSelectedPosition(position)
            }
        }
    }

    class CompanyAdapter(var context: Context) : BaseRecyclerViewAdapter<String, ItemStringBinding>() {
        private var selectedPosition = -1
        fun updateSelectedPosition(newPosition: Int) {
            val previousPosition = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(newPosition)
        }

        override fun onBindDefViewHolder(holder: BaseBindViewHolder<ItemStringBinding>, item: String?, position: Int) {
            if (item == null) {
                return
            }
            holder.binding.itemTextView.text = item
            if (position == selectedPosition) {
                holder.binding.itemTextView.setTextColor(ContextCompat.getColor(context, com.wuc.lib_common.R.color.common_cancel_text_color))
            } else {
                holder.binding.itemTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }

        override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemStringBinding {
            return ItemStringBinding.inflate(layoutInflater, parent, false)
        }

    }
}