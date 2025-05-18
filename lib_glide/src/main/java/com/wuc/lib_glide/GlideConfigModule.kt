package com.wuc.lib_glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.resource.bitmap.Downsampler
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: Glide配置模块, 自定义Glide缓存配置
 *
 * https://mp.weixin.qq.com/s/PoruGnGD0pMAOASQ8iJBog
 */
@GlideModule
class GlideConfigModule : AppGlideModule()  {
    /**
     * 配置Glide选项
     */
    override fun applyOptions(context: Context, builder: GlideBuilder) {
//        super.applyOptions(context, builder)
        // 1. 配置内存缓存 - 动态扩容
        builder.setMemoryCache(DynamicLruCache(context))
        // 2. 配置磁盘缓存 - 分区优化
        configureDiskCache(context, builder)

        // 3. 配置默认请求选项
//        builder.setDefaultRequestOptions(
//            RequestOptions()
//                .format(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    DecodeFormat.PREFER_ARGB_8888
//                } else {
//                    DecodeFormat.PREFER_RGB_565
//                })
//        )
        val bitmapPool = LruBitmapPool(
            Runtime.getRuntime().maxMemory() / 8 // 分配最大内存的1/8作为BitmapPool
        )
        builder.setBitmapPool(bitmapPool)

        // 3. 配置默认请求选项 - 开启RGB_565硬解码
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565) // 使用RGB_565格式，内存占用减少50%
                .set(Downsampler.ALLOW_HARDWARE_CONFIG, true) // 允许硬件加速解码
        )
    }
    /**
     * 配置磁盘缓存 - 分区优化
     */
    private fun configureDiskCache(context: Context, builder: GlideBuilder) {
        // 创建不同存储池
        val smallImageCache = DiskLruCacheWrapper.create(
            File(context.cacheDir, "small_images"),
            20 * 1024 * 1024 // 20MB
        )

        val largeImageCache = DiskLruCacheWrapper.create(
            File(context.cacheDir, "large_images"),
            100 * 1024 * 1024 // 100MB
        )

        // 设置磁盘缓存工厂
        builder.setDiskCache {
            // 根据实际需求可以在这里实现路由逻辑
            // 这里简单返回大图缓存
            largeImageCache
        }
    }
    /**
     * 禁用清单解析以提高性能
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}