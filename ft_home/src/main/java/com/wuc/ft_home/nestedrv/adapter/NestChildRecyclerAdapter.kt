package com.wuc.ft_home.nestedrv.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wuc.ft_home.databinding.ItemNestedChildTextBinding
import com.wuc.lib_base.ext.toast
import com.wuc.lib_base.log.WLogUtils
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter

/**
 * @author: wuc
 * @date: 2024/12/16
 * @description:
 */
class NestChildRecyclerAdapter  : BaseRecyclerViewAdapter<String, ItemNestedChildTextBinding>() {
    override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemNestedChildTextBinding {
        return ItemNestedChildTextBinding.inflate(layoutInflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindDefViewHolder(holder: BaseBindViewHolder<ItemNestedChildTextBinding>, item: String?, position: Int) {
        WLogUtils.i("${item ?: ""} item $position")
        holder.binding.txtTitle.text = "${item ?: ""} item $position"
        holder.binding.txtTitle.minHeight = (200 + position * 1.1f).toInt()
        holder.binding.txtTitle.setOnClickListener {
            toast("click ${item ?: ""} item $position")
        }
    }
}