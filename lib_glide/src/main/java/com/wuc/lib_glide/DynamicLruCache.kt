package com.wuc.lib_glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: 动态内存缓存, 根据设备内存动态计算缓存大小，并为不同尺寸的图片分配不同的权重
 */
class DynamicLruCache(context: Context) : LruResourceCache(
    calculateMemoryCacheSize(context).toLong()
) {
    companion object {
        /**
         * 计算内存缓存大小
         * 根据设备内存和屏幕密度动态计算
         */
        fun calculateMemoryCacheSize(context: Context): Int {
            val calculator = MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2f) // 设置缓存屏幕数量
                .build()
            // 获取默认计算的缓存大小
            val defaultMemoryCacheSize = calculator.memoryCacheSize
            // 根据设备内存动态调整
            val maxMemory = Runtime.getRuntime().maxMemory() / 1024
            val memoryCacheSize = when {
                maxMemory < 64 * 1024 -> defaultMemoryCacheSize / 2 // 低内存设备减半
                maxMemory > 256 * 1024 -> defaultMemoryCacheSize * 2 // 高内存设备翻倍
                else -> defaultMemoryCacheSize
            }
            return memoryCacheSize
        }
    }

    /**
     * 重写sizeOf方法，根据图片尺寸动态计算权重
     */
    override fun getSize(resource: Resource<*>?): Int {
        val value = resource?.get()
        if (value is Bitmap) {
            val bitmapSize = value.byteCount / 1024
            // 根据图片尺寸动态计算权重
            return (bitmapSize * when {
                value.width > 2000 -> 2.0
                value.height > 1000 -> 1.5
                else -> 1.0
            }).toInt()
        }
        return super.getSize(resource)
    }
}