package com.wuc.lib_base

import android.app.Application
import android.content.Context
import android.util.Log
import com.wuc.lib_base.app.LoadModuleProxy
import com.wuc.lib_base.ext.application
import com.wuc.lib_base.helper.AppHelper
import com.wuc.lib_base.network_intercept.NetWorkMonitorManager
import com.wuc.lib_base.network_intercept.NetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/**
 * @author: wuc
 * @date: 2024/10/8
 * @desc: 基类的Application
 */
class BaseApplication : Application() {
    companion object {
        //此变量会在网络监听中被动态赋值
        lateinit var networkType: NetWorkUtil.NetworkType
        //检查当前是否有网络
        fun checkHasNet(): Boolean {
            return networkType != NetWorkUtil.NetworkType.NETWORK_NO && networkType != NetWorkUtil.NetworkType.NETWORK_UNKNOWN
        }
    }
    private val mCoroutineScope by lazy(mode = LazyThreadSafetyMode.NONE) { MainScope() }

    private val mLoadModuleProxy by lazy(mode = LazyThreadSafetyMode.NONE) { LoadModuleProxy() }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        mLoadModuleProxy.onAttachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        //获取到全局的网络状态
        networkType = NetWorkUtil.getNetworkType(this@BaseApplication.applicationContext)
        //网络监听
        NetWorkMonitorManager.init(this)
        AppHelper.init(this)
        mLoadModuleProxy.onCreate(this)
        // 策略初始化第三方依赖
        initDepends()
    }

    /**
     * 初始化第三方依赖
     */
    private fun initDepends() {
        // 开启一个 Default Coroutine 进行初始化不会立即使用的第三方
        mCoroutineScope.launch(Dispatchers.Default) {
            // 调用 mLoadModuleProxy 的 initByBackstage 方法进行后台初始化
            mLoadModuleProxy.initByBackstage()
        }

        // 前台初始化
        val allTimeMillis = measureTimeMillis {
            // 调用 mLoadModuleProxy 的 initByFrontDesk 方法进行前台初始化，并获取初始化依赖项的列表
            val depends = mLoadModuleProxy.initByFrontDesk()
            var dependInfo: String
            // 遍历依赖项列表，逐个初始化
            depends.forEach {
                // 记录每个依赖项初始化所花费的时间
                val dependTimeMillis = measureTimeMillis { dependInfo = it() }
                // 打印日志，记录依赖项的初始化信息和时间
                Log.d("BaseApplication", "initDepends: $dependInfo : $dependTimeMillis ms")
            }
        }
        // 打印日志，记录所有依赖项初始化完成所花费的总时间
        Log.d("BaseApplication", "初始化完成 $allTimeMillis ms")
    }

    override fun onTerminate() {
        super.onTerminate()
        mLoadModuleProxy.onTerminate(this)
        mCoroutineScope.cancel()
    }
}