package com.wuc.lib_base.bitmap

import android.graphics.Bitmap
import android.os.Build
import android.util.LruCache
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Bitmap缓存管理，包含内存缓存和软引用缓存
 */
class BitmapCache {
    companion object {
        @Volatile
        private var instance: BitmapCache? = null
        fun getInstance(): BitmapCache {
            return instance ?: synchronized(this) {
                instance ?: BitmapCache().also { instance = it }
            }
        }
    }

    // 内存缓存，使用LruCache实现
    private val memoryCache: LruCache<String, Bitmap>

    // 软引用缓存，当内存缓存中的Bitmap被回收后，可以从这里找回
    private val softCache = ConcurrentHashMap<String, SoftReference<Bitmap>>()
    // 添加BitmapPool引用
    private val bitmapPool = BitmapPool.getInstance()

    // 添加缓存命中统计
    private var cacheHits = 0
    private var cacheMisses = 0
    private var poolHits = 0

    init {
        // 计算可用内存的1/8作为缓存大小
        val maxMemory = (Runtime.getRuntime().maxMemory() / 8).toInt()
        memoryCache = object : LruCache<String, Bitmap>(maxMemory) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // 返回Bitmap的实际内存占用大小 https://mp.weixin.qq.com/s/WBqoswcKfE2kF4AJY0Yskg
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    bitmap.allocationByteCount
                } else {
                    bitmap.byteCount
                }
            }

            override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap?) {
                // 当Bitmap从内存缓存中移除时，将其放入软引用缓存
                if (!oldValue.isRecycled) {
                    softCache[key] = SoftReference(oldValue)
                }
            }
        }
    }
    /**
     * 将Bitmap放入缓存
     * @param key 缓存键
     * @param bitmap 要缓存的Bitmap
     */
    fun put(key: String, bitmap: Bitmap) {
        if (get(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    /**
     * 从缓存中获取Bitmap
     * @param key 缓存键
     * @return 缓存的Bitmap，如果没有则返回null
     */
//    fun get(key: String): Bitmap? {
//        // 先从内存缓存中获取
//        var bitmap = memoryCache[key]
//
//        // 如果内存缓存中没有，尝试从软引用缓存中获取
//        if (bitmap == null) {
//            val softRef = softCache[key]
//            bitmap = softRef?.get()
//
//            if (bitmap != null) {
//                // 将软引用缓存中的Bitmap重新放入内存缓存
//                memoryCache.put(key, bitmap)
//                softCache.remove(key)
//            }
//        }
//
//        return bitmap
//    }
    /**
     * 从缓存中获取Bitmap
     * @param key 缓存键
     * @param width 目标宽度，用于从复用池获取
     * @param height 目标高度，用于从复用池获取
     * @param config 配置参数，用于从复用池获取
     * @return 缓存的Bitmap，如果没有则返回null
     */
    fun get(key: String, width: Int = 0, height: Int = 0, config: Bitmap.Config = Bitmap.Config.RGB_565): Bitmap? {
        // 先从内存缓存中获取
        var bitmap = memoryCache[key]

        if (bitmap != null) {
            cacheHits++
            return bitmap
        }

        // 如果内存缓存中没有，尝试从软引用缓存中获取
        val softRef = softCache[key]
        bitmap = softRef?.get()

        if (bitmap != null) {
            // 将软引用缓存中的Bitmap重新放入内存缓存
            memoryCache.put(key, bitmap)
            softCache.remove(key)
            cacheHits++
            return bitmap
        }

        // 如果软引用缓存中也没有，且提供了尺寸信息，尝试从复用池获取
        if (width > 0 && height > 0) {
            bitmap = bitmapPool.getBitmap(width, height, config)
            if (bitmap != null) {
                poolHits++
                // 注意：从复用池获取的Bitmap需要重新解码填充内容，这里只返回空白Bitmap
                // 调用方需要负责填充内容
                return bitmap
            }
        }

        cacheMisses++
        return null
    }

    /**
     * 从缓存中移除Bitmap
     * @param key 缓存键
     */
    fun remove(key: String) {
        memoryCache.remove(key)
        softCache.remove(key)
    }

    /**
     * 清空缓存
     */
    fun clear() {
        memoryCache.evictAll()
        softCache.clear()
    }

    /**
     * 清理软引用缓存
     */
    fun clearSoftCache() {
        softCache.clear()
    }

    /**
     * 获取缓存命中率统计
     */
    fun getCacheStats(): String {
        val total = cacheHits + cacheMisses
        val hitRate = if (total > 0) cacheHits.toFloat() / total * 100 else 0f
        val poolHitRate = if (cacheMisses > 0) poolHits.toFloat() / cacheMisses * 100 else 0f
        return "Cache hit rate: ${hitRate.toInt()}%, Pool hit rate: ${poolHitRate.toInt()}%"
    }
}