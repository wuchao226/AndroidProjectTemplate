package com.wuc.lib_base.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import androidx.annotation.IntRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Bitmap管理类，提供Bitmap加载、缓存、复用等功能, 解决常见的OOM问题和内存优化
 */
class BitmapManager {
    companion object {
        private const val TAG = "BitmapManager"

        @Volatile
        private var instance: BitmapManager? = null
        fun getInstance(): BitmapManager = instance ?: synchronized(this) {
            instance ?: BitmapManager().also { instance = it }
        }

        // 默认内存缓存大小为可用内存的1/8
        private val DEFAULT_MEMORY_CACHE_SIZE = (Runtime.getRuntime().maxMemory() / 8).toInt()

        // 默认Bitmap池大小
        private const val DEFAULT_BITMAP_POOL_SIZE = 10
    }

    private val bitmapLoader = BitmapLoader.getInstance()
    private val bitmapCache = BitmapCache.getInstance()
    private val bitmapPool = BitmapPool.getInstance()

    // 区域解码器缓存，避免重复创建
    private val regionDecoderCache = ConcurrentHashMap<String, BitmapRegionDecoder>()

    // 默认配置
    private var defaultConfig = BitmapConfig.DEFAULT
    /**
     * 设置默认配置
     */
    fun setDefaultConfig(config: BitmapConfig) {
        defaultConfig = config
    }

    /**
     * 从资源加载Bitmap
     * @param context 上下文
     * @param resId 资源ID
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @param config 配置参数
     * @return 加载的Bitmap
     */
    fun loadBitmapFromResource(
        context: Context,
        resId: Int, reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = defaultConfig
    ): Bitmap? {
        val cacheKey = "res_${resId}_${reqWidth}_${reqHeight}_${config.preferredConfig.name}"

        // 先从缓存中获取（包括内存缓存和软引用缓存）
        if (config.memoryCacheEnabled) {
            val cachedBitmap = bitmapCache.get(cacheKey)
            if (cachedBitmap != null) {
                return cachedBitmap
            }
        }

        // 从资源加载，此时BitmapLoader会尝试使用复用池中的Bitmap
        val bitmap = bitmapLoader.loadFromResource(context, resId, reqWidth, reqHeight, config)

        // 放入缓存
        if (bitmap != null && config.memoryCacheEnabled) {
            bitmapCache.put(cacheKey, bitmap)
        }
        return bitmap
    }

    /**
     * 从文件加载Bitmap
     * @param file 文件
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @param config 配置参数
     * @return 加载的Bitmap
     */
    fun loadBitmapFromFile(
        file: File,
        reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = defaultConfig
    ): Bitmap? {
        val cacheKey = "file_${file.absolutePath}_${reqWidth}_${reqHeight}_${config.preferredConfig.name}"

        // 先从缓存中获取
        if (config.memoryCacheEnabled) {
            val cachedBitmap = bitmapCache.get(cacheKey)
            if (cachedBitmap != null) {
                return cachedBitmap
            }
        }

        // 从文件加载
        val bitmap = bitmapLoader.loadFromFile(file, reqWidth, reqHeight, config)

        // 放入缓存
        if (bitmap != null && config.memoryCacheEnabled) {
            bitmapCache.put(cacheKey, bitmap)
        }

        return bitmap
    }

    /**
     * 从输入流加载Bitmap
     * @param inputStream 输入流
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @param config 配置参数
     * @return 加载的Bitmap
     */
    fun loadBitmapFromStream(
        inputStream: InputStream,
        reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = defaultConfig
    ): Bitmap? {
        // 从流加载的Bitmap不进行缓存，因为流通常是一次性的
        return bitmapLoader.loadFromStream(inputStream, reqWidth, reqHeight, config)
    }

    suspend fun loadBitmapFromResourceAsync(
        context: Context,
        resId: Int, reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = defaultConfig
    ): Bitmap? = withContext(Dispatchers.IO) {
        loadBitmapFromResource(context, resId, reqWidth, reqHeight, config)
    }

    suspend fun loadBitmapFromFileAsync(
        file: File,
        reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = defaultConfig
    ): Bitmap? = withContext(Dispatchers.IO) {
        loadBitmapFromFile(file, reqWidth, reqHeight, config)
    }


    /**
     * 加载Bitmap区域，适用于大图片
     * @param filePath 文件路径
     * @param rect 要加载的区域
     * @param sampleSize 采样率
     */
    suspend fun loadBitmapRegion(
        filePath: String,
        rect: Rect,
        @IntRange(from = 1) sampleSize: Int = 1,
        config: Bitmap.Config = Bitmap.Config.RGB_565
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // 获取或创建区域解码器
            val decoder = getRegionDecoder(filePath) ?: return@withContext null

            // 解码选项
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = config
            }

            // 同步解码区域，避免并发问题
            synchronized(decoder) {
                decoder.decodeRegion(rect, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取区域解码器，优先从缓存获取
     */
    private fun getRegionDecoder(filePath: String): BitmapRegionDecoder? {
        return regionDecoderCache.getOrPut(filePath) {
            try {
                BitmapRegionDecoder.newInstance(filePath, false)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 回收Bitmap
     * @param bitmap 要回收的Bitmap
     * @param addToPool 是否添加到复用池
     */
    fun recycleBitmap(bitmap: Bitmap?, addToPool: Boolean = true) {
        if (bitmap == null || bitmap.isRecycled) return

        if (addToPool && bitmap.isMutable) {
            // 添加到复用池
            bitmapPool.putBitmap(bitmap)
        } else {
            // 直接回收
            bitmap.recycle()
        }
    }

    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): String {
        return bitmapCache.getCacheStats()
    }

    /**
     * 清空所有缓存和复用池
     */
    fun clearAll() {
        bitmapCache.clear()
        bitmapPool.clear()
    }
}