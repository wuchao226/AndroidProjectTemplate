package com.wuc.lib_base.helper

import android.app.Application
import com.wuc.lib_base.BuildConfig

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc: 提供应用环境
 */
object AppHelper {
    private lateinit var app: Application
    fun init(application: Application) {
        this.app = application
    }

    /**
     * 获取全局应用
     */
    fun getApplication() = app


    /**
     * 当前是否为调试模式
     */
    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    /**
     * 获取当前构建的模式
     */
    fun getBuildType(): String {
        return BuildConfig.BUILD_TYPE
    }
}