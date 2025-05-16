package com.wuc.lib_base.handler

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 生命周期感知的Handler实现, 自动在组件销毁时清理消息，避免内存泄漏和崩溃
 */
class LifecycleHandler : Handler, LifecycleEventObserver {
    private val lifecycle: Lifecycle

    /**
     * 构造函数
     * @param lifecycleOwner 生命周期所有者（如Activity、Fragment）
     */
    constructor(lifecycleOwner: LifecycleOwner) : super(Looper.getMainLooper()) {
        lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(this)
    }

    /**
     * 构造函数（带Callback）
     * @param lifecycleOwner 生命周期所有者
     * @param callback Handler回调
     */
    constructor(lifecycleOwner: LifecycleOwner, callback: Callback) : super(Looper.getMainLooper(), callback) {
        lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        // 当组件被销毁时，自动清理所有消息和回调
        if (event == Lifecycle.Event.ON_DESTROY) {
            removeCallbacksAndMessages(null)
            lifecycle.removeObserver(this)
        }
    }

    /**
     * 检查生命周期状态，确保组件处于活跃状态
     * @return 组件是否处于活跃状态
     */
    private fun isActive(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    /**
     * 安全发送消息，仅在生命周期处于活跃状态时发送
     * @param what 消息类型
     * @return 是否成功发送
     */
    fun sendSafeMessage(what: Int): Boolean {
        if (!isActive()) {
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
        if (!isActive()) {
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
        if (!isActive()) {
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
        if (!isActive()) {
            return false
        }
        return sendMessageDelayed(obtainMessage(what, obj), delayMillis)
    }
}