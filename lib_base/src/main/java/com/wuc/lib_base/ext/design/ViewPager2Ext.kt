package com.wuc.lib_base.ext.design

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:ViewPager2 扩展
 */
fun FragmentActivity.FragmentStateExtAdapter(
    vararg fragments: Fragment,
    isLazyLoading: Boolean = false
): FragmentStateAdapter =
    FragmentStateExtAdapter(fragments.size, isLazyLoading) { fragments[it] }

fun Fragment.FragmentStateAdapter(vararg fragments: Fragment, isLazyLoading: Boolean = false): FragmentStateAdapter =
    FragmentStateExtAdapter(fragments.size, isLazyLoading) { fragments[it] }

fun FragmentActivity.FragmentStateExtAdapter(
    itemCount: Int,
    isLazyLoading: Boolean = false,
    block: (Int) -> Fragment
): FragmentStateAdapter =
    FragmentStateExtAdapter(supportFragmentManager, lifecycle, itemCount, isLazyLoading, block)

fun Fragment.FragmentStateExtAdapter(
    itemCount: Int,
    isLazyLoading: Boolean = false,
    block: (Int) -> Fragment
): FragmentStateAdapter =
    FragmentStateExtAdapter(childFragmentManager, lifecycle, itemCount, isLazyLoading, block)

/**
 * 创建一个 FragmentStateAdapter 的实例。
 * @param fragmentManager FragmentManager 实例，用于管理 Fragment。
 * @param lifecycle Lifecycle 实例，用于处理 Fragment 的生命周期。
 * @param itemCount ViewPager2 中的 Fragment 总数。
 * @param isLazyLoading 是否启用懒加载。如果为 true，则设置 RecyclerView 的缓存大小为 Fragment 总数。
 * @param block 一个函数，根据位置返回相应的 Fragment。
 * @return 返回一个匿名的 FragmentStateAdapter 实例。
 */
fun FragmentStateExtAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    itemCount: Int,
    isLazyLoading: Boolean = false,
    block: (Int) -> Fragment
) =
    object : FragmentStateAdapter(fragmentManager, lifecycle) {
        /**
         * 返回 ViewPager2 中的 Fragment 总数。
         */
        override fun getItemCount() = itemCount

        /**
         * 根据给定的位置创建并返回 Fragment。
         * @param position Fragment 的位置索引。
         * @return 返回通过 block 函数生成的 Fragment。
         */
        override fun createFragment(position: Int) = block(position)

        /**
         * 当 Adapter 被附加到 RecyclerView 时调用。
         * @param recyclerView RecyclerView 实例。
         */
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            // 如果启用了懒加载，设置 RecyclerView 的缓存大小为 Fragment 的总数。
            if (isLazyLoading) recyclerView.setItemViewCacheSize(itemCount)
        }
    }

/**
 * 获取 Fragment
 */
@Suppress("UNCHECKED_CAST")
fun <T : Fragment> ViewPager2.findFragment(fragmentManager: FragmentManager, position: Int) =
    fragmentManager.findFragmentByTag("f$position") as T?