package com.wuc.lib_base.bitmap

import android.graphics.Bitmap

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Bitmap配置类，用于统一管理Bitmap的配置参数
 */
class BitmapConfig {
    companion object {
        // 默认配置
        val DEFAULT = Builder().build()

        // 内存优化配置
        val MEMORY_SAVING = Builder()
            .setPreferredConfig(Bitmap.Config.RGB_565)
            .setInSampleSize(2)
            .build()

        // 高质量配置
        val HIGH_QUALITY = Builder()
            .setPreferredConfig(Bitmap.Config.ARGB_8888)
            .setInSampleSize(1)
            .build()
    }
    // Bitmap配置参数
    var preferredConfig: Bitmap.Config = Bitmap.Config.RGB_565
    var inSampleSize: Int = 1
    var shouldRecycle: Boolean = true
    var memoryCacheEnabled: Boolean = true
    var diskCacheEnabled: Boolean = true
    class Builder {
        private val config = BitmapConfig()

        constructor()

        constructor(baseConfig: BitmapConfig) {
            config.preferredConfig = baseConfig.preferredConfig
            config.inSampleSize = baseConfig.inSampleSize
            config.shouldRecycle = baseConfig.shouldRecycle
            config.memoryCacheEnabled = baseConfig.memoryCacheEnabled
            config.diskCacheEnabled = baseConfig.diskCacheEnabled
        }

        fun setPreferredConfig(preferredConfig: Bitmap.Config): Builder {
            config.preferredConfig = preferredConfig
            return this
        }

        fun setInSampleSize(inSampleSize: Int): Builder {
            config.inSampleSize = inSampleSize
            return this
        }

        fun setShouldRecycle(shouldRecycle: Boolean): Builder {
            config.shouldRecycle = shouldRecycle
            return this
        }

        fun setMemoryCacheEnabled(enabled: Boolean): Builder {
            config.memoryCacheEnabled = enabled
            return this
        }

        fun setDiskCacheEnabled(enabled: Boolean): Builder {
            config.diskCacheEnabled = enabled
            return this
        }

        fun build(): BitmapConfig {
            return config
        }
    }
}