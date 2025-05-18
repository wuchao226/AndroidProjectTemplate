package com.wuc.lib_glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: 创建智能图片加载扩展函数，根据滚动速度动态调整加载策略
 */

/**
 * 智能加载图片
 * 根据滚动速度动态调整加载策略
 * @param url 图片URL
 * @param scrollSpeed 滚动速度
 */
fun ImageView.loadSmartImage(url: String?, scrollSpeed: Int = 0) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions()
                // 根据滑动速度动态调整优先级
                .priority(
                    when (scrollSpeed) {
                        in 0..1000 -> Priority.HIGH
                        in 1001..3000 -> Priority.NORMAL
                        else -> Priority.LOW
                    }
                )
                // 开启智能降级 - 高速滑动时加载缩略图
                .override(
                    if (scrollSpeed > 2000) {
                        100 // 高速滑动时加载小图
                    } else {
                        Target.SIZE_ORIGINAL
                    }
                )
                .placeholder(R.mipmap.default_img)
                .error(R.mipmap.default_img)
                .skipMemoryCache(false) // 启用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        )
        .into(this)
}

/**
 * 预加载图片
 * @param urls 需要预加载的图片URL列表
 */
fun preloadCriticalImages(context: Context, urls: List<String>, width: Int = 200, height: Int = 200) {
    urls.forEach { url ->
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .preload(width, height)
    }
}