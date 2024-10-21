package com.wuc.ft_home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.wuc.ft_home.databinding.ActivityHomeBinding
import com.wuc.ft_home.databinding.LayoutBottomTabBinding
import com.wuc.ft_home.fragment.FIND_INDEX
import com.wuc.ft_home.fragment.FindFragment
import com.wuc.ft_home.fragment.FragmentIndex
import com.wuc.ft_home.fragment.HOME_INDEX
import com.wuc.ft_home.fragment.HomeFragment
import com.wuc.ft_home.fragment.MESSAGE_INDEX
import com.wuc.ft_home.fragment.MINE_INDEX
import com.wuc.ft_home.fragment.MessageFragment
import com.wuc.ft_home.fragment.MineFragment
import com.wuc.lib_base.ext.design.doOnCustomTabSelected
import com.wuc.lib_base.ext.design.setCustomView
import com.wuc.lib_base.ext.pressBackTwiceToExitApp
import com.wuc.lib_base.log.WLogUtils
import com.wuc.lib_common.base.activity.BaseViewBindingReflectActivity

class HomeActivity : BaseViewBindingReflectActivity<ActivityHomeBinding>() {

    private var fragmentIndex: Int = 0

    companion object {
        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"

        @JvmOverloads
        fun start(context: Context, @FragmentIndex fragmentIndex: Int = HOME_INDEX) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_FRAGMENT_INDEX, fragmentIndex)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

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
                WLogUtils.d("createFragment: position = $position, fragment=${tabList[position].fragment}")
                return tabList[position].fragment
            }
        }
        // smoothScroll = false -> 关闭平滑滚动效果
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2, false, false) { tab, position ->
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
        mBinding.viewPager2.currentItem = HOME_INDEX
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        fragmentIndex = intent.getIntExtra(INTENT_KEY_IN_FRAGMENT_INDEX, 0)
        switchFragment(fragmentIndex)
    }

    private fun switchFragment(fragmentIndex: Int) {
        WLogUtils.d("switchFragment: fragmentIndex = $fragmentIndex")
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            HOME_INDEX, FIND_INDEX, MESSAGE_INDEX, MINE_INDEX -> {
//                mBinding.viewPager2.currentItem = fragmentIndex
                mBinding.tabLayout.getTabAt(fragmentIndex)?.select()
            }
        }
    }

    data class CustomTab(
        val title: Int,
        val icon: Int,
        val fragment: Fragment
    )
}