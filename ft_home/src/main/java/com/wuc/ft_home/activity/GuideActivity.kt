package com.wuc.ft_home.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.gyf.immersionbar.ImmersionBar
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityGuideBinding
import com.wuc.ft_home.databinding.GuideItemBinding
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter
import com.wuc.lib_common.base.activity.BaseBindingReflectActivity

class GuideActivity : BaseBindingReflectActivity<ActivityGuideBinding>() {

    private val adapter: GuideAdapter = GuideAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        adapter.setData(listOf(R.drawable.guide_1_bg, R.drawable.guide_2_bg, R.drawable.guide_3_bg))
        binding.vpGuidePager.adapter = adapter
        binding.vpGuidePager.registerOnPageChangeCallback(mCallback)
        binding.cvGuideIndicator.setViewPager(binding.vpGuidePager)
        binding.btnGuideComplete.doOnDebouncingClick {
            HomeActivity.start(this)
            finish()
        }
    }
    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // 指定导航栏背景颜色
            .navigationBarColor(R.color.white)
    }

    private val mCallback: OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            if (binding.vpGuidePager.currentItem != adapter.itemCount - 1 || positionOffsetPixels <= 0) {
                return
            }
            binding.cvGuideIndicator.visibility = View.VISIBLE
            binding.btnGuideComplete.visibility = View.INVISIBLE
            binding.btnGuideComplete.clearAnimation()
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return
            }
            val lastItem = binding.vpGuidePager.currentItem == adapter.itemCount - 1
            binding.cvGuideIndicator.visibility = if (lastItem) View.INVISIBLE else View.VISIBLE
            binding.btnGuideComplete.visibility = if (lastItem) View.VISIBLE else View.INVISIBLE
            if (lastItem) {
                // 按钮呼吸动效
                val animation = ScaleAnimation(
                    1.0f, 1.1f, 1.0f, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                )
                animation.duration = 350
                animation.repeatMode = Animation.REVERSE
                animation.repeatCount = Animation.INFINITE
                binding.btnGuideComplete.startAnimation(animation)
            }
        }
    }

    inner class GuideAdapter : BaseRecyclerViewAdapter<Int, GuideItemBinding>() {

        override fun getViewBinding(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): GuideItemBinding {
            return GuideItemBinding.inflate(layoutInflater, parent, false)
        }

        override fun onBindDefViewHolder(holder: BaseBindViewHolder<GuideItemBinding>, item: Int?, position: Int) {
            if (item == null) {
                return
            }
            holder.binding.apply {
                ivGuide.setImageResource(item)
            }
        }
    }

    override fun onDestroy() {
        binding.vpGuidePager.unregisterOnPageChangeCallback(mCallback)
        super.onDestroy()
    }
}