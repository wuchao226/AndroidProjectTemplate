package com.wuc.lib_base.livedata.demo

import androidx.appcompat.app.AppCompatActivity

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 在Activity中使用
 */
class ActivityDemo : AppCompatActivity() {
    fun demo() {
        // 订阅事件
        HomeEvents.UserProfile.UPDATE.observe(this)
        { userData ->
            // 处理用户信息更新
//            updateUserUI(userData)
        }

        // 发布事件
//        HomeEvents.UserProfile.UPDATE.setValue(newUserData)
    }
}