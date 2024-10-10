package com.wuc.lib_base.ext

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
import com.wuc.lib_base.utils.datastore.IDataStoreOwner

/**
 * @author: wuc
 * @date: 2024/10/8
 * @desc:
 */
class AppInitializer : Initializer<Unit> {

    companion object {
        internal var onAppStatusChangedListener: OnAppStatusChangedListener? = null
        private var started = 0
    }

    override fun create(context: Context) {
        application = context as Application
        IDataStoreOwner.application = context as Application
        MMKV.initialize(context)
        // 注册APP前后台切换监听
        appFrontBackRegister()
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
    /**
     * 注册APP前后台切换监听
     */
    private fun appFrontBackRegister() {
        application.doOnActivityLifecycle(
            onActivityCreated = { activity, _ ->
                activityCache.add(activity)
            },
            onActivityStarted = { activity ->
                started++
                if (started == 1) {
                    onAppStatusChangedListener?.onForeground(activity)
                }
            },
            onActivityStopped = { activity ->
                started--
                if (started == 0) {
                    onAppStatusChangedListener?.onBackground(activity)
                }
            },
            onActivityDestroyed = { activity ->
                activityCache.remove(activity)
            }
        )
    }
}