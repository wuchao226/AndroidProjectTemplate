package com.wuc.ft_home

import android.app.Application
import android.content.Context
import com.google.auto.service.AutoService
import com.wuc.ft_home.helper.CrashHandler
import com.wuc.lib_base.app.ApplicationLifecycle
import com.wuc.lib_base.ext.application
import com.wuc.lib_base.utils.ProcessUtils

/**
 * @author: wuc
 * @date: 2024/10/23
 * @description:
 */
@AutoService(ApplicationLifecycle::class)
class HomeApplication: ApplicationLifecycle {
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
        list.add { initCrashHandler() }
        return list
    }

    /**
     * 不需要立即初始化的放在这里进行后台初始化
     */
    override fun initByBackstage() {

    }

    private fun initCrashHandler(): String {
        // 本地异常捕捉
        CrashHandler.register(application)
        return "CrashHandler -->> init complete"
    }
}