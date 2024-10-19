package com.wuc.ft_home

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wuc.ft_home.databinding.ActivityHomeBinding
import com.wuc.ft_home.databinding.LayoutBottomTabBinding
import com.wuc.ft_home.fragment.FindFragment
import com.wuc.ft_home.fragment.HomeFragment
import com.wuc.ft_home.fragment.MessageFragment
import com.wuc.ft_home.fragment.MineFragment
import com.wuc.lib_base.ext.design.doOnCustomTabSelected
import com.wuc.lib_base.ext.design.setCustomView
import com.wuc.lib_base.ext.pressBackTwiceToExitApp
import com.wuc.lib_common.base.activity.BaseViewBindingReflectActivity

class HomeActivity : BaseViewBindingReflectActivity<ActivityHomeBinding>() {

    private val tabList: List<CustomTab> by lazy {
        listOf(
            CustomTab(R.string.home_nav_index, R.drawable.home_home_selector, HomeFragment.newInstance()),
            CustomTab(R.string.home_nav_found, R.drawable.home_found_selector, FindFragment.newInstance()),
            CustomTab(R.string.home_nav_message, R.drawable.home_message_selector, MessageFragment.newInstance()),
            CustomTab(R.string.home_nav_me, R.drawable.home_me_selector, MineFragment.newInstance())
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        pressBackTwiceToExitApp(toastText = R.string.home_exit_hint)

        mBinding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = tabList.size

            override fun createFragment(position: Int): Fragment {
                return tabList[position].fragment
            }
        }
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2, false) { tab, position ->
            tab.setCustomView<LayoutBottomTabBinding> {
                tvTitle.text = getString(tabList[position].title)
                ivIcon.setImageResource(tabList[position].icon)
            }
        }.attach()
        mBinding.tabLayout.doOnCustomTabSelected<LayoutBottomTabBinding>(
            onTabSelected = { tab ->
                if (tab.position == 2) {
                    ivUnreadState.isVisible = true
                }
            }
        )
        // 禁止viewpager2左右滑动
        mBinding.viewPager2.isUserInputEnabled = false

    }

    data class CustomTab(
        val title: Int,
        val icon: Int,
        val fragment: Fragment
    )
}