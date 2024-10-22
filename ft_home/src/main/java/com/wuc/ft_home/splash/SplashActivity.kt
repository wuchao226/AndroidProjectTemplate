package com.wuc.ft_home.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import androidx.core.view.isVisible
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.databinding.ActivitySplashBinding
import com.wuc.lib_base.ext.disableBackPressed
import com.wuc.lib_base.helper.AppHelper
import com.wuc.lib_common.base.activity.BaseViewBindingReflectActivity
import java.util.Locale

/**
 * @author: wuc
 * @date: 2024/10/21
 * @description: 闪屏界面
 */
class SplashActivity : BaseViewBindingReflectActivity<ActivitySplashBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        // 禁用返回
        disableBackPressed(this)
        // 设置动画监听
        mBinding.lavSplashLottie.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mBinding.lavSplashLottie.removeAnimatorListener(this)
                HomeActivity.start(this@SplashActivity)
                finish()
            }
        })

    }

    override fun initData() {
        super.initData()
        mBinding.ivSplashDebug.let {
            if (AppHelper.isDebug()) {
                // 显示 Debug 信息
                it.isVisible = true
                it.setText(AppHelper.getBuildType().uppercase(Locale.getDefault()))
            } else {
                it.isVisible = false
            }
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }
}