package com.wuc.lib_base.ext

import android.os.Handler
import android.os.Looper

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
val mainThreadHandler by lazy { Handler(Looper.getMainLooper()) }

/**
 * 是否在主线程
 */
val isMainThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

fun mainThread(block: () -> Unit) {
    if (isMainThread) block() else mainThreadHandler.post(block)
}

/**
 * 在主线程运行
 * mainThread([delayMillis]) {...}
 */
fun mainThread(delayMillis: Long, block: () -> Unit) =
    mainThreadHandler.postDelayed(block, delayMillis)