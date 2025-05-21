package com.wuc.lib_base.bitmap

import android.graphics.Bitmap
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Bitmap - 扩展方法
 */

// 添加协程支持的异步加载扩展函数
suspend fun ImageView.loadBitmapAsync(resId: Int, config: BitmapConfig = BitmapConfig.DEFAULT) {
    val bitmap = BitmapManager.getInstance().loadBitmapFromResourceAsync(
        context,
        resId,
        width.takeIf { it > 0 } ?: 500,
        height.takeIf { it > 0 } ?: 500,
        config
    )
    withContext(Dispatchers.Main) {
        setImageBitmap(bitmap)
    }
}

// 添加带加载回调的扩展函数
fun ImageView.loadBitmapWithCallback(
    resId: Int,
    config: BitmapConfig = BitmapConfig.DEFAULT,
    callback: (success: Boolean, bitmap: Bitmap?) -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val bitmap = BitmapManager.getInstance().loadBitmapFromResourceAsync(
                context,
                resId,
                width.takeIf { it > 0 } ?: 500,
                height.takeIf { it > 0 } ?: 500,
                config
            )
            setImageBitmap(bitmap)
            callback(bitmap != null, bitmap)
        } catch (e: Exception) {
            callback(false, null)
        }
    }
}


/**
 * 加载资源图片
 * @param resId 资源ID
 * @param config 配置参数
 */
fun ImageView.loadBitmap(resId: Int, config: BitmapConfig = BitmapConfig.DEFAULT) {
    val bitmap = BitmapManager.getInstance().loadBitmapFromResource(
        context,
        resId,
        width.takeIf { it > 0 } ?: 500,
        height.takeIf { it > 0 } ?: 500,
        config
    )
    setImageBitmap(bitmap)
}

/**
 * 加载文件图片
 * @param file 文件
 * @param config 配置参数
 */
fun ImageView.loadBitmap(file: File, config: BitmapConfig = BitmapConfig.DEFAULT) {
    val bitmap = BitmapManager.getInstance().loadBitmapFromFile(
        file,
        width.takeIf { it > 0 } ?: 500,
        height.takeIf { it > 0 } ?: 500,
        config
    )
    setImageBitmap(bitmap)
}

/**
 * 加载内存优化的图片（使用RGB_565格式和2倍采样率）
 * @param resId 资源ID
 */
fun ImageView.loadMemoryOptimizedBitmap(resId: Int) {
    loadBitmap(resId, BitmapConfig.MEMORY_SAVING)
}

/**
 * 加载高质量图片（使用ARGB_8888格式和1倍采样率）
 * @param resId 资源ID
 */
fun ImageView.loadHighQualityBitmap(resId: Int) {
    loadBitmap(resId, BitmapConfig.HIGH_QUALITY)
}

/**
 * 加载无缓存的图片
 * @param resId 资源ID
 */
fun ImageView.loadNoCacheBitmap(resId: Int) {
    val config = BitmapConfig.Builder()
        .setMemoryCacheEnabled(false)
        .setDiskCacheEnabled(false)
        .build()
    loadBitmap(resId, config)
}