package com.wuc.ft_home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityBadgeDrawableBinding
import com.wuc.ft_home.toolbar.ToolbarActivity

class BadgeDrawableActivity : ToolbarActivity<ActivityBadgeDrawableBinding>() {


    override fun setToolbar() {
        mToolbar.setTitle(R.string.badge_drawable)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initTabLayout()
        initTextView()
        initButton()
        initImageView()
        initNavigationView()
    }

    private fun initTabLayout() {
        // 带数字小红点
        binding.tabLayout.getTabAt(0)?.let {
            it.orCreateBadge.apply {
                backgroundColor = ContextCompat.getColor(this@BadgeDrawableActivity, com.wuc.lib_common.R.color.red)
                badgeTextColor = ContextCompat.getColor(this@BadgeDrawableActivity, R.color.white)
                number = 6
            }
        }

        // 不带数字小红点
        binding.tabLayout.getTabAt(1)?.let {
            it.orCreateBadge.apply {
                backgroundColor = ContextCompat.getColor(this@BadgeDrawableActivity, com.wuc.lib_common.R.color.red)
                badgeTextColor = ContextCompat.getColor(this@BadgeDrawableActivity, R.color.white)
            }
        }
    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun initTextView() {
        // 在视图树变化
        binding.tvBadge.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                BadgeDrawable.create(this@BadgeDrawableActivity).apply {
                    // 设置基于目标view的位置
                    badgeGravity = BadgeDrawable.TOP_END
                    number = 6
//                    backgroundColor = ContextCompat.getColor(this@BadgeDrawableActivity, R.color.colorPrimary)
                    backgroundColor = getAttrColorPrimary()
                    isVisible = true
                    horizontalOffset = -10
                    BadgeUtils.attachBadgeDrawable(this, binding.tvBadge)
                }
                binding.tvBadge.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
    private fun initButton() {
        binding.mbBadge.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onGlobalLayout() {
                BadgeDrawable.create(this@BadgeDrawableActivity).apply {
                    badgeGravity = BadgeDrawable.TOP_START
                    number = 6
                    backgroundColor = ContextCompat.getColor(this@BadgeDrawableActivity, com.wuc.lib_common.R.color.red)
                    // MaterialButton本身有间距，不设置为0dp的话，可以设置badge的偏移量
                    verticalOffset = 15
                    horizontalOffset = 10
                    BadgeUtils.attachBadgeDrawable(this, binding.mbBadge, binding.flBtn)
                }
                binding.mbBadge.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
    private fun initImageView() {
        binding.sivBadge.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onGlobalLayout() {
                BadgeDrawable.create(this@BadgeDrawableActivity).apply {
                    badgeGravity = BadgeDrawable.TOP_END
                    number = 99999
                    // badge最多显示字符，默认999+ 是4个字符（带'+'号）
                    maxCharacterCount = 3
                    backgroundColor = ContextCompat.getColor(this@BadgeDrawableActivity, com.wuc.lib_common.R.color.red)
                    BadgeUtils.attachBadgeDrawable(this, binding.sivBadge, binding.flImg)
                }
                binding.sivBadge.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
    private fun initNavigationView() {
        // BottomNavigationView奇怪的选中背景: https://juejin.cn/post/7114493272000561166
        binding.navigationView.isItemActiveIndicatorEnabled = false
        binding.navigationView.itemIconTintList = null
        binding.navigationView.getOrCreateBadge(R.id.navigation_home).apply {
            backgroundColor = ContextCompat.getColor(this@BadgeDrawableActivity, com.wuc.lib_common.R.color.red)
            badgeTextColor = ContextCompat.getColor(this@BadgeDrawableActivity, R.color.white)
            number = 9999
        }
    }
}
