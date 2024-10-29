package com.wuc.ft_home.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.databinding.adapters.TextViewBindingAdapter.setTextSize
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ImmersionBar
import com.wuc.lib_common.R
import com.wuc.ft_home.databinding.FragmentHomeBinding
import com.wuc.ft_home.databinding.LayoutHomeTabBinding
import com.wuc.lib_base.ext.design.FragmentStateExtAdapter
import com.wuc.lib_base.ext.design.doOnCustomTabSelected
import com.wuc.lib_base.ext.design.doOnTabSelected
import com.wuc.lib_base.ext.design.doOnTabUnselected
import com.wuc.lib_base.ext.design.setCustomView
import com.wuc.lib_base.ext.design.setupWithViewPager2
import com.wuc.lib_base.ext.dp
import com.wuc.lib_common.base.fragment.TitleBarFragment
import com.wuc.lib_common.widget.layout.NestCollapsingToolbarLayout

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description: 首页
 */
class HomeFragment : TitleBarFragment<FragmentHomeBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment().apply {}
    }

    private val tabList: List<CustomHomeTab> by lazy {
        listOf(
            CustomHomeTab("列表演示", ListFragment.newInstance()),
            CustomHomeTab("网页演示", ListFragment.newInstance()),
        )
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(requireActivity(), binding.tbHomeTitle)
        binding.viewPager2.adapter = FragmentStateExtAdapter(tabList.size) { position ->
            tabList[position].fragment
        }
        binding.tabLayout.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.setCustomView<LayoutHomeTabBinding> {
                tabItemTextView.text = tabList[position].title
            }
            handlerTabLayoutBold(tab, position == 0)
        }
        binding.tabLayout.doOnCustomTabSelected<LayoutHomeTabBinding>(
            onTabSelected = { tab ->
                handlerTabLayoutBold(tab, true)
            },
            onTabUnselected = { tab ->
                handlerTabLayoutBold(tab, false)
            }
        )
        // 初始状态下，选中第一个 Tab 项
        binding.tabLayout.getTabAt(0)?.select()
        // 设置渐变监听
        binding.ctlHomeBar.setOnScrimsListener(object : NestCollapsingToolbarLayout.OnScrimsListener {
            @SuppressLint("RestrictedApi")
            override fun onScrimsStateChange(layout: NestCollapsingToolbarLayout, shown: Boolean) {
                getStatusBarConfig().statusBarDarkFont(shown).init()
                binding.tvHomeAddress.setTextColor(
                    ContextCompat.getColor(
                        getAttachActivity()!!,
                        if (shown) R.color.black else R.color.white
                    )
                )
                binding.tvHomeHint.setBackgroundResource(
                    if (shown) com.wuc.ft_home.R.drawable.home_search_bar_gray_bg
                    else com.wuc.ft_home.R.drawable.home_search_bar_transparent_bg
                )
                binding.tvHomeHint.setTextColor(
                    ContextCompat.getColor(
                        getAttachActivity()!!,
                        if (shown) R.color.black60 else R.color.white60
                    )
                )
                binding.ivHomeSearch.supportImageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        if (shown) R.color.common_icon_color else R.color.white
                    )
                )
            }
        })
    }

    override fun isStatusBarDarkFont(): Boolean {
        return binding.ctlHomeBar.isScrimsShown()
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    /** 修改单独某一个的tab大小加粗效果*/
   private fun handlerTabLayoutBold(tab: TabLayout.Tab?, isBold: Boolean) {
        tab?.view?.forEach { view ->
            if (view is TextView) {
                view.apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
                    if (isBold) {
                        typeface = Typeface.DEFAULT_BOLD
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.common_accent_color))
                    } else {
                        typeface = Typeface.DEFAULT
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.black25))
                    }
                }
            }
        }
    }

    data class CustomHomeTab(
        val title: String,
        val fragment: Fragment
    )
}