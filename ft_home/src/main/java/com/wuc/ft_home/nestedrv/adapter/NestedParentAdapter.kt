package com.wuc.ft_home.nestedrv.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.wuc.ft_home.databinding.ItemNestedInnerBinding
import com.wuc.ft_home.databinding.ItemNestedParentBinding
import com.wuc.ft_home.nestedrv.bean.ItemParent
import com.wuc.ft_home.nestedrv.fragment.NestedChildFragment
import com.wuc.lib_base.ext.getCompatDrawable
import com.wuc.lib_base.ext.screenWidth
import com.wuc.lib_base.ext.toast
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseMultiItemAdapter
import com.wuc.lib_common.adapter.BaseViewHolder


/**
 * @author: wuc
 * @date: 2024/12/16
 * @description:
 */
class NestedParentAdapter(private val activity: AppCompatActivity) : BaseMultiItemAdapter<ItemParent>() {
    companion object {
        const val TYPE_ITEM: Int = 0

        const val TYPE_INNER: Int = 1
    }

    private val tabs: List<String> =
        mutableListOf("推荐", "热点", "视频", "直播", "社会", "娱乐", "科技", "汽车", "体育", "财经", "军事", "国际", "时尚", "游戏", "旅游", "历史", "探索", "美食", "育儿", "养生", "故事", "美文")

    override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewBinding {
        return when(viewType){
            TYPE_ITEM -> ItemNestedParentBinding.inflate(layoutInflater, parent, false)
            TYPE_INNER -> ItemNestedInnerBinding.inflate(layoutInflater, parent, false)
            else -> ItemNestedParentBinding.inflate(layoutInflater, parent, false)
        }
    }

    override fun onCreateDefViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == TYPE_ITEM) {
            return BaseBindViewHolder(getViewBinding(layoutInflater, parent, viewType))
        } else {
            val innerBinding = getViewBinding(layoutInflater, parent, viewType) as ItemNestedInnerBinding
            return BaseBindViewHolder(getViewBinding(layoutInflater, parent, viewType))
        }
    }

    override fun onBindDefViewHolder(holder: BaseBindViewHolder<ViewBinding>, item: ItemParent?, position: Int) {
        if (item == null) {
            return
        }
        if (item.itemType == TYPE_ITEM) {
            val drawable = getCompatDrawable(item.imgId)
            val width = drawable?.intrinsicWidth ?: 0
            val height = drawable?.intrinsicHeight ?: 0
            val targetHeight: Int =  screenWidth * height / width
            val layoutParams: ViewGroup.LayoutParams = holder.itemView.layoutParams
            layoutParams.height = targetHeight
            val parentBinding = holder.binding as ItemNestedParentBinding
            parentBinding.imageView.setImageDrawable(drawable)
            parentBinding.imageView.setOnClickListener {
                toast("点击了第 ${holder.bindingAdapterPosition} 个")
            }
        } else {
            val innerBinding = holder.binding as ItemNestedInnerBinding
            innerBinding.viewPager.offscreenPageLimit = 5
            val fragments = SparseArray<Fragment>()
            tabs.forEachIndexed { index, tab ->
                fragments.put(index, NestedChildFragment.newInstance(tab))
            }
            innerBinding.viewPager.adapter = NestedChildAdapter(activity, fragments)
            TabLayoutMediator(innerBinding.tabLayout, innerBinding.viewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
    }
}