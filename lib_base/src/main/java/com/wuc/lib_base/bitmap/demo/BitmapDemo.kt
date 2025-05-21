package com.wuc.lib_base.bitmap.demo

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description:
 */
class BitmapDemo {
   /* // 1. 基本使用
    imageView.loadBitmap(R.drawable.large_image)

    // 2. 内存优化模式
    imageView.loadMemoryOptimizedBitmap(R.drawable.large_image)

    // 3. 高质量模式
    imageView.loadHighQualityBitmap(R.drawable.large_image)

    // 4. 自定义配置
    val config = BitmapConfig.Builder()
        .setPreferredConfig(Bitmap.Config.RGB_565) // 使用RGB_565格式节省内存
        .setInSampleSize(2) // 2倍采样率
        .setMemoryCacheEnabled(true) // 启用内存缓存
        .build()

    imageView.loadBitmap(R.drawable.large_image, config)

    // 5. 从文件加载
    val file = File("/storage/emulated/0/Download/image.jpg")
    imageView.loadBitmap(file)

    // 6. 直接使用BitmapManager
    val bitmap = BitmapManager.getInstance().loadBitmapFromResource(
        context,
        R.drawable.large_image,
        500, // 目标宽度
        500, // 目标高度
        BitmapConfig.MEMORY_SAVING
    )

    // 使用完毕后回收
    BitmapManager.getInstance().recycleBitmap(bitmap)*/
}