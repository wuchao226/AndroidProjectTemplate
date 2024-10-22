package com.wuc.ft_home.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityGuideBinding
import com.wuc.ft_home.databinding.GuideItemBinding
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_base.log.WLogUtils
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter
import com.wuc.lib_common.base.activity.BaseViewBindingReflectActivity

class GuideActivity : BaseViewBindingReflectActivity<ActivityGuideBinding>() {

    private val adapter: GuideAdapter = GuideAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        adapter.setData(listOf(R.drawable.guide_1_bg, R.drawable.guide_2_bg, R.drawable.guide_3_bg))
        mBinding.vpGuidePager.adapter = adapter
        mBinding.vpGuidePager.registerOnPageChangeCallback(mCallback)
        mBinding.btnGuideComplete.doOnDebouncingClick {
            HomeActivity.start(this)
            finish()
        }
    }

    private val mCallback: OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            WLogUtils.it(
                "guide",
                "position = $position, positionOffset = $positionOffset, positionOffsetPixels = $positionOffsetPixels"
            )
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return
            }
            val lastItem = mBinding.vpGuidePager.currentItem == adapter.itemCount - 1
            mBinding.btnGuideComplete.isVisible = lastItem
            if (lastItem) {
                // 按钮呼吸动效
                val animation = ScaleAnimation(
                    1.0f, 1.1f, 1.0f, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                )
                animation.duration = 350
                animation.repeatMode = Animation.REVERSE
                animation.repeatCount = Animation.INFINITE
                mBinding.btnGuideComplete.startAnimation(animation)
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
        mBinding.vpGuidePager.unregisterOnPageChangeCallback(mCallback)
        super.onDestroy()
    }
}