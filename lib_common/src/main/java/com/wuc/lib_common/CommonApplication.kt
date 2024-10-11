package com.wuc.lib_common

import android.app.Application
import android.content.Context
import com.google.auto.service.AutoService
import com.tencent.mmkv.MMKV
import com.wuc.lib_base.BaseApplication
import com.wuc.lib_base.app.ApplicationLifecycle
import com.wuc.lib_base.ext.application
import com.wuc.lib_base.utils.ProcessUtils

/**
 * @author: wuc
 * @date: 2024/10/10
 * @desc: 项目相关的Application
 */
@AutoService(ApplicationLifecycle::class)
class CommonApplication : ApplicationLifecycle {
    /**
     * 同[Application.attachBaseContext]
     * @param context Context
     */
    override fun onAttachBaseContext(context: Context) {

    }

    /**
     * 同[Application.onCreate]
     * @param application Application
     */
    override fun onCreate(application: Application) {}

    /**
     * 同[Application.onTerminate]
     * @param application Application
     */
    override fun onTerminate(application: Application) {}

    /**
     * 主线程前台初始化
     * @return MutableList<() -> String> 初始化方法集合
     */
    override fun initByFrontDesk(): MutableList<() -> String> {
        val list = mutableListOf<() -> String>()
        // 以下只需要在主进程当中初始化 按需要调整
        if (ProcessUtils.isMainProcess(application)) {
            list.add { initMMKV() }
            list.add { initTheRouter() }
        }
        return list
    }
    /**
     * 不需要立即初始化的放在这里进行后台初始化
     */
    override fun initByBackstage() {

    }

    /**
     * 腾讯 MMKV 初始化
     */
    private fun initMMKV(): String {
        MMKV.initialize(application)
        return "MMKV -->> "
    }
    /**
     * 阿里路由 ARouter 初始化
     */
    private fun initTheRouter(): String {

        return "TheRouter -->> init complete"
    }
}