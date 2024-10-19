package com.wuc.lib_base.ext.design

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wuc.lib_base.ext.viewbinding.getBinding

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:TabLayout 扩展类
 */
/**
 * 设置 ViewPager
 */
fun TabLayout.setupWithViewPager(
    viewPager: ViewPager,
    autoRefresh: Boolean = true,
    tabConfigurationStrategy: (TabLayout.Tab, Int) -> Unit
) {
    setupWithViewPager(viewPager, autoRefresh)
    for (i in 0 until tabCount) {
        getTabAt(i)?.let { tab ->
            tabConfigurationStrategy(tab, i)
        }
    }
}

/**
 * 设置 ViewPager2
 */
/**
 * 这个函数将 TabLayout 与 ViewPager2 进行设置。
 *
 * @param viewPager 要与 TabLayout 关联的 ViewPager2 实例。
 * @param autoRefresh 如果为 true，当 ViewPager2 的适配器更改时，TabLayout 将自动刷新。
 * @param enableScroll 如果为 true，ViewPager2 将允许用户输入进行滚动。
 * @param tabConfigurationStrategy 一个 lambda 函数，允许对每个标签进行自定义配置。它接受一个 TabLayout.Tab 和它的位置作为参数。
 */
fun TabLayout.setupWithViewPager2(
    viewPager: ViewPager2,
    autoRefresh: Boolean = true,
    enableScroll: Boolean = true,
    tabConfigurationStrategy: (TabLayout.Tab, Int) -> Unit
) {
    // 启用或禁用 ViewPager2 上的用户输入滚动
    viewPager.isUserInputEnabled = enableScroll
    TabLayoutMediator(this, viewPager, autoRefresh, enableScroll, tabConfigurationStrategy).attach()
}

/**
 * 设置自定义布局
 */
inline fun TabLayout.Tab.setCustomView(@LayoutRes layoutId: Int, block: View.() -> Unit) {
    setCustomView(layoutId)
    block(customView!!)
}

inline fun <reified VB : ViewBinding> TabLayout.Tab.setCustomView(block: VB.() -> Unit) {
    requireNotNull(parent) { "Tab not attached to a TabLayout" }
    inflateBinding<VB>(LayoutInflater.from(parent!!.context)).apply(block).let { binding ->
        customView = binding.root
        customView?.tag = binding
    }
}

inline fun <reified VB : ViewBinding> TabLayout.updateCustomTab(index: Int, block: VB.() -> Unit) =
    getTabAt(index)?.customView?.getBinding<VB>()?.also(block)

inline fun <reified VB : ViewBinding> TabLayout.doOnCustomTabSelected(
    crossinline onTabUnselected: VB.(TabLayout.Tab) -> Unit = {},
    crossinline onTabReselected: VB.(TabLayout.Tab) -> Unit = {},
    crossinline onTabSelected: VB.(TabLayout.Tab) -> Unit = {},
) =
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            tab.customView?.getBinding<VB>()?.onTabSelected(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            tab.customView?.getBinding<VB>()?.onTabUnselected(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            tab.customView?.getBinding<VB>()?.onTabReselected(tab)
        }
    })

/**
 * 添加标签
 */
inline fun TabLayout.addTab(@StringRes resId: Int, crossinline block: TabLayout.Tab.() -> Unit = {}) =
    addTab(context.getString(resId), block)

inline fun TabLayout.addTab(text: String? = null, crossinline block: TabLayout.Tab.() -> Unit = {}) =
    addTab(newTab().apply { this.text = text }.apply(block))

/**
 * 监听标签被选中
 */
inline fun TabLayout.doOnTabSelected(crossinline block: (TabLayout.Tab) -> Unit) =
    addOnTabSelectedListener(onTabSelected = block)

/**
 * 监听标签取消选中
 */
inline fun TabLayout.doOnTabUnselected(crossinline block: (TabLayout.Tab) -> Unit) =
    addOnTabSelectedListener(onTabUnselected = block)

/**
 * 监听标签重新选中
 */
inline fun TabLayout.doOnTabReselected(crossinline block: (TabLayout.Tab) -> Unit) =
    addOnTabSelectedListener(onTabReselected = block)

/**
 * 监听标签选中事件
 */
inline fun TabLayout.addOnTabSelectedListener(
    crossinline onTabSelected: (TabLayout.Tab) -> Unit = {},
    crossinline onTabUnselected: (TabLayout.Tab) -> Unit = {},
    crossinline onTabReselected: (TabLayout.Tab) -> Unit = {},
) =
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) = onTabSelected(tab)

        override fun onTabUnselected(tab: TabLayout.Tab) = onTabUnselected(tab)

        override fun onTabReselected(tab: TabLayout.Tab) = onTabReselected(tab)
    })
