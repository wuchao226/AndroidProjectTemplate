package com.wuc.lib_base.bitmap

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description:
 */
class BitmapLifecycleObserver : LifecycleEventObserver{
    companion object {
        fun bind(lifecycle: Lifecycle) {
            lifecycle.addObserver(BitmapLifecycleObserver())
        }
    }
    fun onAppBackground() {
        // 应用进入后台，清理部分缓存
        BitmapManager.getInstance().clearAll()
    }

    fun onActivityDestroyed() {
        // Activity销毁，可以考虑清理与该Activity相关的缓存
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_STOP -> onAppBackground()
            Lifecycle.Event.ON_DESTROY -> onActivityDestroyed()
            else -> {}
        }
    }
}