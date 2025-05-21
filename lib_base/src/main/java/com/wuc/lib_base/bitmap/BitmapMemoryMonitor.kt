package com.wuc.lib_base.bitmap

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: 内存监控与自适应调整
 */
class BitmapMemoryMonitor private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: BitmapMemoryMonitor? = null
        fun getInstance(context: Context): BitmapMemoryMonitor {
            return instance ?: synchronized(this) {
                instance ?: BitmapMemoryMonitor(context.applicationContext).also { instance = it }
            }
        }
        // 内存状态常量
        const val MEMORY_STATE_NORMAL = 0    // 正常状态
        const val MEMORY_STATE_WARNING = 1   // 警告状态
        const val MEMORY_STATE_CRITICAL = 2  // 危险状态

        // 内存阈值
        private const val WARNING_THRESHOLD = 0.7f  // 70%内存使用率触发警告
        private const val CRITICAL_THRESHOLD = 0.85f // 85%内存使用率触发危险
    }

    // 使用WeakReference避免内存泄漏
    private val contextRef = WeakReference(context)
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    // 当前内存状态
    private var currentMemoryState = MEMORY_STATE_NORMAL

    // 内存状态监听器列表
    private val memoryStateListeners = mutableListOf<MemoryStateListener>()

    // 定时检查内存状态的Handler
    private val handler = Handler(Looper.getMainLooper())
    private val memoryCheckRunnable = object : Runnable {
        override fun run() {
            checkMemoryState()
            // 每30秒检查一次内存状态
            handler.postDelayed(this, 30_000)
        }
    }

    /**
     * 开始监控内存状态
     */
    fun startMonitoring() {
        handler.post(memoryCheckRunnable)
    }

    /**
     * 停止监控内存状态
     */
    fun stopMonitoring() {
        handler.removeCallbacks(memoryCheckRunnable)
    }

    /**
     * 添加内存状态监听器
     */
    fun addMemoryStateListener(listener: MemoryStateListener) {
        if (!memoryStateListeners.contains(listener)) {
            memoryStateListeners.add(listener)
        }
    }

    /**
     * 移除内存状态监听器
     */
    fun removeMemoryStateListener(listener: MemoryStateListener) {
        memoryStateListeners.remove(listener)
    }


    /**
     * 内存状态监听接口
     */
    interface MemoryStateListener {
        fun onMemoryStateChanged(newState: Int)
    }

    /**
     * 检查内存状态
     */
    fun checkMemoryState() {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val availableMemory = memoryInfo.availMem
        val totalMemory = memoryInfo.totalMem
        val memoryUsageRatio = 1 - (availableMemory.toFloat() / totalMemory)

        val newState = when {
            memoryUsageRatio >= CRITICAL_THRESHOLD -> MEMORY_STATE_CRITICAL
            memoryUsageRatio >= WARNING_THRESHOLD -> MEMORY_STATE_WARNING
            else -> MEMORY_STATE_NORMAL
        }

        if (newState != currentMemoryState) {
            currentMemoryState = newState
            notifyMemoryStateChanged(newState)
        }
    }

    /**
     * 通知所有监听器内存状态变化
     */
    private fun notifyMemoryStateChanged(newState: Int) {
        for (listener in memoryStateListeners) {
            listener.onMemoryStateChanged(newState)
        }

        // 根据内存状态自动调整BitmapManager配置
        adjustBitmapConfig(newState)
    }

    /**
     * 根据内存状态调整Bitmap配置
     */
    private fun adjustBitmapConfig(memoryState: Int) {
        val bitmapManager = BitmapManager.getInstance()

        when (memoryState) {
            MEMORY_STATE_NORMAL -> {
                // 正常状态：使用默认配置
                bitmapManager.setDefaultConfig(BitmapConfig.DEFAULT)
            }
            MEMORY_STATE_WARNING -> {
                // 警告状态：使用内存优化配置
                bitmapManager.setDefaultConfig(BitmapConfig.Builder()
                    .setPreferredConfig(Bitmap.Config.RGB_565) // 使用更省内存的配置
                    .setMemoryCacheEnabled(true)
                    .setInSampleSize(2) // 增加采样率，降低分辨率
                    .build())

                // 清理部分缓存
//                bitmapManager.trimMemory()
            }
            MEMORY_STATE_CRITICAL -> {
                // 危险状态：使用极度节省内存的配置
                bitmapManager.setDefaultConfig(BitmapConfig.Builder()
                    .setPreferredConfig(Bitmap.Config.RGB_565)
                    .setMemoryCacheEnabled(false) // 关闭内存缓存
                    .setInSampleSize(4) // 大幅降低分辨率
                    .build())

                // 清理所有缓存
                bitmapManager.clearAll()
            }
        }
    }


    /**
     * 获取当前内存使用情况报告
     */
    fun getMemoryReport(): String {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val availableMemory = memoryInfo.availMem / (1024 * 1024) // MB
        val totalMemory = memoryInfo.totalMem / (1024 * 1024) // MB
        val usedMemory = totalMemory - availableMemory
        val memoryUsageRatio = usedMemory.toFloat() / totalMemory * 100

        return "内存使用: ${usedMemory}MB/${totalMemory}MB (${memoryUsageRatio.toInt()}%)"
    }



    private val memoryThresholds = listOf(
        0.8f, // 内存使用率超过80%，采取激进措施
        0.7f, // 内存使用率超过70%，采取中等措施
        0.6f  // 内存使用率超过60%，采取轻度措施
    )

    // 获取当前内存使用率
    fun getMemoryUsageRatio(): Float {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        return usedMemory.toFloat() / maxMemory
    }

    // 根据内存使用情况调整配置
    fun getAdaptiveConfig(): BitmapConfig {
        val memoryRatio = getMemoryUsageRatio()
        return when {
            memoryRatio >= memoryThresholds[0] -> {
                // 激进措施：使用RGB_565，高采样率，禁用内存缓存
                BitmapConfig.Builder()
                    .setPreferredConfig(Bitmap.Config.RGB_565)
                    .setInSampleSize(4)
                    .setMemoryCacheEnabled(false)
                    .build()
            }

            memoryRatio >= memoryThresholds[1] -> {
                // 中等措施：使用RGB_565，中等采样率
                BitmapConfig.Builder()
                    .setPreferredConfig(Bitmap.Config.RGB_565)
                    .setInSampleSize(2)
                    .build()
            }

            memoryRatio >= memoryThresholds[2] -> {
                // 轻度措施：默认配置但增加采样率
                BitmapConfig.Builder()
                    .setInSampleSize(2)
                    .build()
            }

            else -> {
                // 内存充足，使用默认配置
                BitmapConfig.DEFAULT
            }
        }
    }

    // 在内存紧张时清理缓存
    fun cleanupIfNeeded() {
        val memoryRatio = getMemoryUsageRatio()
        if (memoryRatio >= memoryThresholds[0]) {
            // 内存严重不足，清理所有缓存
            BitmapManager.getInstance().clearAll()
        } else if (memoryRatio >= memoryThresholds[1]) {
            // 内存较紧张，只清理软引用缓存
            BitmapCache.getInstance().clearSoftCache()
        }
    }
}