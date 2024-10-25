package com.wuc.lib_common

import android.app.Application
import android.content.Context
import android.view.Gravity
import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.hjq.toast.Toaster
import com.tencent.mmkv.MMKV
import com.wuc.lib_base.app.ApplicationLifecycle
import com.wuc.lib_base.ext.application
import com.wuc.lib_base.ext.isAppDebug
import com.wuc.lib_base.helper.ToastLogInterceptor
import com.wuc.lib_base.log.LogConfig
import com.wuc.lib_base.log.LogConfig.JsonParser
import com.wuc.lib_base.log.LogManager
import com.wuc.lib_base.log.printer.ConsolePrinter
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
        list.add { initLog() }
        // 以下只需要在主进程当中初始化 按需要调整
        if (ProcessUtils.isMainProcess(application)) {
            list.add { initMMKV() }
            list.add { initTheRouter() }
            list.add { initToaster() }
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
    /**
     * Toast 框架 初始化
     */
    private fun initToaster(): String {
        // 初始化 Toast 框架
        Toaster.init(application)
        // 自定义 Toast 拦截器
        Toaster.setInterceptor(ToastLogInterceptor())
        return "Toaster -->> init complete"
    }

    /**
     * 初始化 Log
     */
    private fun initLog(): String {
        LogManager.init(
            object : LogConfig() {
                override fun injectJsonParser(): JsonParser {
                    return JsonParser { src -> Gson().toJson(src) }
                }

                override fun includeThread(): Boolean = true

                override fun enable(): Boolean = isAppDebug

                override fun stackTraceDepth(): Int = 7
            },
            ConsolePrinter()
        )
        return "Log -->> init complete"
    }
}