package com.wuc.lib_base.bitmap

import android.graphics.Bitmap
import android.util.LruCache
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Bitmap复用池，用于复用Bitmap对象，减少内存分配和GC
 */
class BitmapPool private constructor() {
    companion object {
        @Volatile
        private var instance: BitmapPool? = null
        fun getInstance(): BitmapPool = instance ?: synchronized(this) {
            instance ?: BitmapPool().also { instance = it }
        }

        // 添加大小限制和超时清理机制
        private val maxPoolSize = 20 // 每种规格最大缓存数量
        private val lastAccessTime = ConcurrentHashMap<String, Long>() // 记录每个规格最后访问时间
        private val cleanupInterval = 60_000L // 清理间隔，毫秒
        private var lastCleanupTime = System.currentTimeMillis()
    }

    // 生成缓存键
    private fun generateKey(width: Int, height: Int, config: Bitmap.Config): String {
        return "${width}_${height}_${config.name}"
    }

    private val bitmapCache = LruCache<String, LinkedList<Bitmap>>((Runtime.getRuntime().maxMemory() / 8).toInt())

    /**
     * 获取可复用的Bitmap
     * @param width 宽度
     * @param height 高度
     * @param config 配置
     * @return 可复用的Bitmap，如果没有则返回null
     */
    fun getBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        val key = generateKey(width, height, config)
        // 更新最后访问时间
        lastAccessTime[key] = System.currentTimeMillis()
        val bitmapList = bitmapCache[key] ?: return null

        synchronized(bitmapList) {
            if (bitmapList.isNotEmpty()) {
                val bitmap = bitmapList.removeFirst()
                if (!bitmap.isRecycled) {
                    bitmap.eraseColor(0) // 清空Bitmap内容
                    return bitmap
                }
            }
        }
        return null
    }

    /**
     * 将Bitmap放入复用池
     * @param bitmap 要放入的Bitmap
     */
    fun putBitmap(bitmap: Bitmap) {
//        if (bitmap.isRecycled) return
//
//        val key = generateKey(bitmap.width, bitmap.height, bitmap.config)
//        var bitmapList = bitmapCache[key]
//
//        if (bitmapList == null) {
//            bitmapList = LinkedList()
//            bitmapCache.put(key, bitmapList)
//        }
//
//        synchronized(bitmapList) {
//            bitmapList.addLast(bitmap)
//        }

        if (bitmap.isRecycled) return

        // 检查是否需要清理过期缓存
        checkCleanup()

        val key = generateKey(bitmap.width, bitmap.height, bitmap.config)
        var bitmapList = bitmapCache[key]

        if (bitmapList == null) {
            bitmapList = LinkedList()
            bitmapCache.put(key, bitmapList)
        }

        synchronized(bitmapList) {
            // 限制每种规格的Bitmap数量
            if (bitmapList.size >= maxPoolSize) {
                val oldBitmap = bitmapList.removeFirst()
                if (!oldBitmap.isRecycled) {
                    oldBitmap.recycle()
                }
            }
            bitmapList.addLast(bitmap)
        }

        // 更新最后访问时间
        lastAccessTime[key] = System.currentTimeMillis()
    }

    // 检查是否需要清理过期缓存
    private fun checkCleanup() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastCleanupTime > cleanupInterval) {
            cleanupExpiredBitmaps()
            lastCleanupTime = currentTime
        }
    }

    // 清理长时间未使用的Bitmap
    private fun cleanupExpiredBitmaps() {
        val currentTime = System.currentTimeMillis()
        val expireTime = 5 * 60 * 1000L // 5分钟未使用则清理

        val keysToCheck = ArrayList(lastAccessTime.keys)
        for (key in keysToCheck) {
            val lastAccess = lastAccessTime[key] ?: continue
            if (currentTime - lastAccess > expireTime) {
                val bitmapList = bitmapCache[key] ?: continue
                synchronized(bitmapList) {
                    for (bitmap in bitmapList) {
                        if (!bitmap.isRecycled) {
                            bitmap.recycle()
                        }
                    }
                    bitmapList.clear()
                }
                bitmapCache.remove(key)
                lastAccessTime.remove(key)
            }
        }
    }

    /**
     * 清空复用池
     */
    fun clear() {
        for (key in bitmapCache.snapshot().keys) {
            val bitmapList = bitmapCache[key] ?: continue
            synchronized(bitmapList) {
                for (bitmap in bitmapList) {
                    if (!bitmap.isRecycled) {
                        bitmap.recycle()
                    }
                }
                bitmapList.clear()
            }
        }
        bitmapCache.evictAll()
    }
}
