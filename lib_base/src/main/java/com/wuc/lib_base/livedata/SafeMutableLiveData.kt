package com.wuc.lib_base.livedata

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 安全的MutableLiveData实现，具有防重放机制
 * 参考：多模块数据混乱？LiveData总线污染的三种防御架构设计
 * https://mp.weixin.qq.com/s/RtknO2azaOv2pBUw8GnMvw
 */
class SafeMutableLiveData<T> : MutableLiveData<T>() {
    // 事件版本号，每次setValue时递增
    private var eventVersion = AtomicLong(0)

    // 记录每个Observer接收到的最新事件版本号
    private val observerVersions = ConcurrentHashMap<Observer<*>, Long>()

    /**
     * 带生命周期感知的观察方法
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // 使用AutoCleanObserver包装原始Observer
        val wrappedObserver = VersionedObserver(observer)
        val autoCleanObserver = AutoCleanObserver(
            this,
            WeakReference(owner as? Context ?: (owner as? Fragment)?.context),
            wrappedObserver,
            owner
        )
        // 调用父类的observe方法
        super.observe(owner, autoCleanObserver)
    }

    /**
     * 设置值并更新版本号
     */
    override fun setValue(value: T) {
        // 递增版本号
        eventVersion.incrementAndGet()
        super.setValue(value)
    }

    /**
     * 带版本控制的Observer
     */
    private inner class VersionedObserver(private val actualObserver: Observer<in T>) : Observer<T> {
        override fun onChanged(value: T) {
            val currentVersion = eventVersion.get()
            val lastVersion = observerVersions.getOrDefault(actualObserver, -1L)
            // 只有当前版本大于Observer已接收的版本时才通知
            if (currentVersion > lastVersion) {
                actualObserver.onChanged(value)
                observerVersions[actualObserver] = currentVersion
            }
        }

    }
}