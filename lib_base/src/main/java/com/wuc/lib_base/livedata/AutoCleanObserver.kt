package com.wuc.lib_base.livedata

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description:  自动清理的Observer，当生命周期组件销毁时自动解除订阅
 */
class AutoCleanObserver<T>(
    private val liveData: LiveData<T>,
    private val moduleContext: WeakReference<Context>,
    private val observer: Observer<T>,
    owner: LifecycleOwner
) : Observer<T>, LifecycleEventObserver {
    init {
        // 添加生命周期观察
        owner.lifecycle.addObserver(this)
    }

    override fun onChanged(value: T) {
        // 检查Context是否已销毁
        val context = moduleContext.get()
        if (context == null) {
            // Context已被回收，自动解除订阅
            liveData.removeObserver(this)
            return
        }
        // 将事件传递给实际的Observer
        observer.onChanged(value)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            // 生命周期结束时自动解除订阅
            liveData.removeObserver(this)
            source.lifecycle.removeObserver(this)
        }
    }
}