package com.wuc.ft_home.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.databinding.ActivitySplashBinding
import com.wuc.lib_base.ext.disableBackPressed
import com.wuc.lib_base.ext.isAppDebug
import com.wuc.lib_base.helper.AppHelper
import com.wuc.lib_common.base.activity.BaseBindingReflectActivity
import java.util.Locale

/**
 * @author: wuc
 * @date: 2024/10/21
 * @description: 闪屏界面
 */
class SplashActivity : BaseBindingReflectActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot) {
            val intent: Intent? = intent
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (((intent != null) && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                        && (Intent.ACTION_MAIN == intent.action))) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish()
                return
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 禁用返回
        disableBackPressed(this)
        // 设置动画监听
        binding.lavSplashLottie.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.lavSplashLottie.removeAnimatorListener(this)
                HomeActivity.start(this@SplashActivity)
                finish()
            }
        })
        binding.ivSplashDebug.let {
            if (isAppDebug) {
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