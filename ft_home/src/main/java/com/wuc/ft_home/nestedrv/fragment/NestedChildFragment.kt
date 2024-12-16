package com.wuc.ft_home.nestedrv.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wuc.ft_home.databinding.ItemNestedChildFragmentBinding
import com.wuc.ft_home.nestedrv.adapter.NestChildRecyclerAdapter
import com.wuc.lib_common.base.fragment.BaseBindingReflectFragment

/**
 * @author: wuc
 * @date: 2024/12/16
 * @description:
 */
class NestedChildFragment private constructor(): BaseBindingReflectFragment<ItemNestedChildFragmentBinding>() {
    companion object {
        fun newInstance(title: String): NestedChildFragment {
            return NestedChildFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
        }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        val title = arguments?.getString("title")
        val innerAdapter = NestChildRecyclerAdapter()
        binding.childRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.set(10, 10, 10, 10)
                }
            })
            adapter = innerAdapter
        }

        val list = mutableListOf<String>()
        for (i in 0 until 100) {
            list.add(title ?: "--")
        }
        innerAdapter.setData(list)
    }
}