package com.wuc.ft_home.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.R
import com.wuc.ft_home.splash.SplashActivity
import com.wuc.lib_base.ext.toast
import com.wuc.lib_common.base.activity.AbsActivity

/**
 * @author: wuc
 * @date: 2024/10/23
 * @description: 重启应用
 */
class RestartActivity : AbsActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RestartActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun restart(context: Context) {
            val intent: Intent = if (true) {
                // 如果是未登录的情况下跳转到闪屏页
                Intent(context, SplashActivity::class.java)
            } else {
                // 如果是已登录的情况下跳转到首页
                Intent(context, HomeActivity::class.java)
            }
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun getLayoutResId(): Int {
        return 0
    }

    override fun initView(savedInstanceState: Bundle?) {
        restart(this)
        finish()
        toast(R.string.common_crash_hint)
    }
}