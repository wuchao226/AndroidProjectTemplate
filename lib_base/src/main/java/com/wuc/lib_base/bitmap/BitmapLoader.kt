package com.wuc.lib_base.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.InputStream

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Bitmap加载器，负责从不同来源加载Bitmap
 */
class BitmapLoader private constructor() {
    companion object {
        private const val TAG = "BitmapLoader"

        @Volatile
        private var instance: BitmapLoader? = null
        fun getInstance(): BitmapLoader {
            return instance ?: synchronized(this) {
                instance ?: BitmapLoader().also { instance = it }
            }
        }
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
    fun loadFromResource(
        context: Context, resId: Int,
        reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = BitmapConfig.DEFAULT
    ): Bitmap? {
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, resId, options)

            // 计算采样率，考虑设备DPI与资源目录的关系 https://mp.weixin.qq.com/s/bjasFdblKMW4MWAJ9dG4oA
            val displayMetrics = context.resources.displayMetrics
            val targetDensity = displayMetrics.densityDpi
            val sourceDensity = options.inTargetDensity

            // 计算实际采样率
            val densityFactor = if (sourceDensity > 0) targetDensity / sourceDensity.toFloat() else 1f
            val effectiveSampleSize = (config.inSampleSize / densityFactor).toInt().coerceAtLeast(1)

            options.inSampleSize = effectiveSampleSize
            options.inJustDecodeBounds = false
            options.inPreferredConfig = config.preferredConfig
            options.inMutable = true // 使Bitmap可变，便于复用

            // 尝试从缓存系统获取可复用的Bitmap
            if (config.memoryCacheEnabled) {
                val outWidth = options.outWidth / options.inSampleSize
                val outHeight = options.outHeight / options.inSampleSize

                // 先尝试从BitmapCache获取（包含了对复用池的查询）
                val cacheKey = "res_${resId}_${reqWidth}_${reqHeight}_${config.preferredConfig.name}"
                val cachedBitmap = BitmapCache.getInstance().get(cacheKey, outWidth, outHeight, config.preferredConfig)

                if (cachedBitmap != null && cachedBitmap.isMutable) {
                    options.inBitmap = cachedBitmap
                }
            }
            return BitmapFactory.decodeResource(context.resources, resId, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load bitmap from resource: $resId", e)
            return null
        }
    }

    /**
     * 从文件加载Bitmap
     * @param file 文件
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @param config 配置参数
     * @return 加载的Bitmap
     */
    fun loadFromFile(
        file: File,
        reqWidth: Int, reqHeight: Int,
        config: BitmapConfig = BitmapConfig.DEFAULT
    ): Bitmap? {
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, options)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, config.inSampleSize)
            options.inJustDecodeBounds = false
            options.inPreferredConfig = config.preferredConfig
            options.inMutable = true

            // 尝试从复用池获取Bitmap
            if (config.memoryCacheEnabled) {
                val outWidth = options.outWidth / options.inSampleSize
                val outHeight = options.outHeight / options.inSampleSize
                options.inBitmap = BitmapPool.getInstance().getBitmap(outWidth, outHeight, config.preferredConfig)
            }

            return BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load bitmap from file: ${file.absolutePath}", e)
            return null
        }
    }

    /**
     * 从输入流加载Bitmap
     * @param inputStream 输入流
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @param config 配置参数
     * @return 加载的Bitmap
     */
    fun loadFromStream(inputStream: InputStream, reqWidth: Int, reqHeight: Int, config: BitmapConfig = BitmapConfig.DEFAULT): Bitmap? {
        try {
            // 由于InputStream不能重用，需要先将其转换为字节数组
            val bytes = inputStream.readBytes()

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, config.inSampleSize)
            options.inJustDecodeBounds = false
            options.inPreferredConfig = config.preferredConfig
            options.inMutable = true

            // 尝试从复用池获取Bitmap
            if (config.memoryCacheEnabled) {
                val outWidth = options.outWidth / options.inSampleSize
                val outHeight = options.outHeight / options.inSampleSize
                options.inBitmap = BitmapPool.getInstance().getBitmap(outWidth, outHeight, config.preferredConfig)
            }

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load bitmap from stream", e)
            return null
        }
    }

    /**
     * 计算采样率
     * @param options BitmapFactory.Options
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @param baseSampleSize 基础采样率
     * @return 计算后的采样率
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int, baseSampleSize: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = baseSampleSize

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // 计算最大的inSampleSize值，该值是2的幂，并且保持高度和宽度大于请求的高度和宽度
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}