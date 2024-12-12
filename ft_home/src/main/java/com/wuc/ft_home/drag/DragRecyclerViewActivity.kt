package com.wuc.ft_home.drag

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityDragRecyclerViewBinding
import com.wuc.ft_home.databinding.ItemDragGridBinding
import com.wuc.ft_home.toolbar.ToolbarActivity
import com.wuc.lib_base.ext.toast
import com.wuc.lib_base.log.WLogUtils
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter
/**
  * @author: wuc
  * @date: 2024/12/12
  * @description:
 * ItemTouchHelper实现RecyclerView拖拽&合并的效果:https://juejin.cn/post/7146499773829218312
 * 实现RecyclerView的上下拖拽: https://juejin.cn/post/7110408776477310989
 * RecyclerView高级特性——拖拽排序以及滑动删除: https://blog.yorek.xyz/android/other/RecyclerView-Sort%26Delete/
  */
class DragRecyclerViewActivity : ToolbarActivity<ActivityDragRecyclerViewBinding>() {

    private val tag = "DragRecyclerViewActivity"

    companion object {
        private var SPAN_COUNT = 4
    }

    private lateinit var mAdapter: DragAdapter
    private lateinit var mGridItemDecoration: GridSpaceItemDecoration
    override fun setToolbar() {
        mToolbar.setTitle(R.string.drag_recyclerview)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.recycleView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        if (!::mGridItemDecoration.isInitialized) {
            mGridItemDecoration = GridSpaceItemDecoration(SPAN_COUNT)
            binding.recycleView.addItemDecoration(mGridItemDecoration, 0)
        }
        mAdapter = DragAdapter()
        val list = getDatas()
        mAdapter.setData(list)
        binding.recycleView.adapter = mAdapter
        // 设置拖拽/滑动
        val dragCallBack = DragCallBack(mAdapter, list)
        val itemTouchHelper = ItemTouchHelper(dragCallBack)
        itemTouchHelper.attachToRecyclerView(binding.recycleView)
        mAdapter.onItemClickListener = { view, position ->
            toast(dragCallBack.getData()[position])
        }
        mAdapter.onItemLongClickListener = { holder, view, position ->
            if (position != mAdapter.fixedPosition) {
                itemTouchHelper.startDrag(holder)
            }
            false
        }
        setListener()
    }

    private fun setListener() {
        binding.tvSwitch.setOnClickListener {
            when (binding.recycleView.layoutManager) {
                is GridLayoutManager -> {
                    binding.recycleView.layoutManager = LinearLayoutManager(this)
                }

                else -> {
                    binding.recycleView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
                }
            }
        }
        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                WLogUtils.it(tag, "onItemRangeMoved")
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                WLogUtils.it(tag, "onItemRangeRemoved")
            }

            override fun onChanged() {
                super.onChanged()
                WLogUtils.it(tag, "onChanged")
            }
        })
    }

    private fun getDatas(): MutableList<String> {
        return mutableListOf(
            "推荐",
            "Android",
            "iOS",
            "前端",
            "后端",
            "音视频",
            "大数据",
            "人工智能",
            "云原生",
            "运维",
            "算法",
            "代码人生"
        )
    }

    inner class DragAdapter : BaseRecyclerViewAdapter<String, ItemDragGridBinding>() {
        val fixedPosition = 0 // 固定菜单
        override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemDragGridBinding {
            return ItemDragGridBinding.inflate(layoutInflater, parent, false)
        }

        override fun onBindDefViewHolder(holder: BaseBindViewHolder<ItemDragGridBinding>, item: String?, position: Int) {
            if (item == null) {
                return
            }
            val binding = holder.binding
            binding.itemTextView.text = item
            // 第一个固定菜单
            val drawable = binding.itemTextView.background as GradientDrawable
            if (holder.bindingAdapterPosition == 0) {
                drawable.color = ContextCompat.getColorStateList(this@DragRecyclerViewActivity, R.color.greenAccent)
            } else {
                drawable.color = ContextCompat.getColorStateList(this@DragRecyclerViewActivity, R.color.greenPrimary)
            }
        }
    }
}