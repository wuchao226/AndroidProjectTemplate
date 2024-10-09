package com.wuc.lib_base.helper

import android.app.Application

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc: 提供应用环境
 */
object AppHelper {
    private lateinit var app: Application
    private var isDebug = false
    fun init(application: Application, isDebug: Boolean) {
        this.app = application
        this.isDebug = isDebug
    }

    /**
     * 获取全局应用
     */
    fun getApplication() = app

    /**
     * 是否为debug环境
     */
    fun isDebug() = isDebug
}