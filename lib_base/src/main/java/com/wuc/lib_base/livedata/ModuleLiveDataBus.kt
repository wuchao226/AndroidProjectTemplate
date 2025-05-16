package com.wuc.lib_base.livedata

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 模块化LiveDataBus，通过三级命名空间隔离防止Key冲突
 * 通过模块级别的Key隔离，防止不同模块使用相同Key导致的数据污染
 *
 * 有效解决LiveData总线在多模块开发中的污染问题：
 *
 * 1. 命名空间隔离：通过三级命名空间（模块名_子域_事件名）防止Key冲突
 * 2. 生命周期绑定：通过AutoCleanObserver自动解除订阅，防止内存泄漏
 * 3. 防重放机制：通过事件版本号防止历史事件被误消费
 * 这种实现方式参考了美团WMRouter和阿里ARouter的核心设计思想，适合在多模块Android项目中安全使用LiveData
 */
object ModuleLiveDataBus {
    // 使用ConcurrentHashMap保证线程安全
    private val busMap = ConcurrentHashMap<String, MutableLiveData<*>>()

    /**
     * 构建三级命名空间Key
     * @param module 模块名
     * @param domain 子域名
     * @param event 事件名
     */
    private fun buildKey(module: String, domain: String, event: String): String {
        return "${module}_${domain}_$event"
    }

    /**
     * 获取指定通道的LiveData
     * @param module 模块名
     * @param domain 子域名
     * @param event 事件名
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getChannel(module: String, domain: String, event: String): MutableLiveData<T> {
        val key = buildKey(module, domain, event)
        return busMap.getOrPut(key) { SafeMutableLiveData<T>() } as MutableLiveData<T>
    }

    /**
     * 移除指定通道
     */
    fun removeChannel(module: String, domain: String, event: String) {
        val key = buildKey(module, domain, event)
        busMap.remove(key)
    }

    /**
     * 移除指定模块的所有通道
     */
    fun removeModuleChannels(module: String) {
        val iterator = busMap.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key.startsWith("${module}_")) {
                iterator.remove()
            }
        }
    }
}