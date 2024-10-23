package com.wuc.ft_home.helper

import android.app.Application
import android.os.Process
import com.wuc.ft_home.activity.CrashActivity
import com.wuc.ft_home.activity.RestartActivity
import com.wuc.lib_base.ext.isAppDebug
import com.wuc.lib_base.utils.mmkv.MMKVUtil
import kotlinx.coroutines.MainScope

/**
 * @author: wuc
 * @date: 2024/10/23
 * @description: Crash 处理类
 */
class CrashHandler private constructor(private val application: Application) : Thread.UncaughtExceptionHandler{

    companion object {
        /** Crash 文件名 */
        private const val CRASH_FILE_NAME: String = "crash_file"

        /** Crash 时间记录 */
        private const val KEY_CRASH_TIME: String = "key_crash_time"

        /**
         * 注册 Crash 监听
         */
        fun register(application: Application) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(application))
        }
    }

    // 上次崩溃的时间
    private var lastCrashTime=0L
    private val nextHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
    private val mCoroutineScope by lazy(mode = LazyThreadSafetyMode.NONE) { MainScope() }

    init {
        if ((javaClass.name == nextHandler?.javaClass?.name)) {
            // 请不要重复注册 Crash 监听
            throw IllegalStateException("are you ok?")
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val currentCrashTime = System.currentTimeMillis()
        lastCrashTime = MMKVUtil.get(KEY_CRASH_TIME, 0L)
        // 记录当前崩溃的时间，以便下次崩溃时进行比对
        MMKVUtil.put(KEY_CRASH_TIME, currentCrashTime)
        // 致命异常标记：如果上次崩溃的时间距离当前崩溃小于 5 分钟，那么判定为致命异常
        val deadlyCrash = currentCrashTime - lastCrashTime < 1000 * 60 * 5
        if (isAppDebug) {
            CrashActivity.start(application, throwable)
        } else {
            if (!deadlyCrash) {
                // 如果不是致命的异常就自动重启应用
                RestartActivity.start(application)
            }
        }

        // 不去触发系统的崩溃处理（com.android.internal.os.RuntimeInit$KillApplicationHandler）
        if (nextHandler != null && !nextHandler.javaClass.name.startsWith("com.android.internal.os")) {
            nextHandler.uncaughtException(thread, throwable)
        }
        // 杀死进程（这个事应该是系统干的，但是它会多弹出一个崩溃对话框，所以需要我们自己手动杀死进程）
        Process.killProcess(Process.myPid())
        System.exit(10)
    }
}