package com.wuc.lib_base.handler

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 安全的Handler实现，避免内存泄漏
 * 通过弱引用持有外部Activity/Fragment，防止Handler持有Activity引用导致的内存泄漏
 */
class SafeHandler : Handler {
    private val activityRef: WeakReference<Activity>

    /**
     * 构造函数
     * @param activity 需要弱引用的Activity
     */
    constructor(activity: Activity) : super(Looper.getMainLooper()) {
        activityRef = WeakReference(activity)
    }

    /**
     * 构造函数（带Callback）
     * @param activity 需要弱引用的Activity
     * @param callback Handler回调
     */
    constructor(activity: Activity, callback: Callback) : super(Looper.getMainLooper(), callback) {
        activityRef = WeakReference(activity)
    }

    override fun handleMessage(msg: Message) {
        if (!isActivityAvailable()) {
            return
        }
        // Activity存在，安全处理消息
        try {
            super.handleMessage(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 检查Activity是否可用
     * @return Activity是否可用
     */
    private fun isActivityAvailable(): Boolean {
        val activity = activityRef.get()
        return activity != null && !activity.isFinishing
    }

    /**
     * 安全发送消息，检查Activity是否存在
     * @param what 消息类型
     * @return 是否成功发送
     */
    fun sendSafeMessage(what: Int): Boolean {
        if (!isActivityAvailable()) {
            return false
        }
        return sendMessage(obtainMessage(what))
    }

    /**
     * 安全发送带参数的消息
     * @param what 消息类型
     * @param obj 消息参数
     * @return 是否成功发送
     */
    fun sendSafeMessage(what: Int, obj: Any): Boolean {
        if (!isActivityAvailable()) {
            return false
        }
        return sendMessage(obtainMessage(what, obj))
    }


    /**
     * 安全延迟发送消息
     * @param what 消息类型
     * @param delayMillis 延迟时间（毫秒）
     * @return 是否成功发送
     */
    fun sendSafeMessageDelayed(what: Int, delayMillis: Long): Boolean {
        if (!isActivityAvailable()) {
            return false
        }
        return sendMessageDelayed(obtainMessage(what), delayMillis)
    }

    /**
     * 安全延迟发送带参数的消息
     * @param what 消息类型
     * @param obj 消息参数
     * @param delayMillis 延迟时间（毫秒）
     * @return 是否成功发送
     */
    fun sendSafeMessageDelayed(what: Int, obj: Any, delayMillis: Long): Boolean {
        if (!isActivityAvailable()) {
            return false
        }
        return sendMessageDelayed(obtainMessage(what, obj), delayMillis)
    }

    /**
     * 清理所有消息和回调
     * 应在Activity的onDestroy中调用
     */
    fun clearAll() {
        removeCallbacksAndMessages(null)
    }
}