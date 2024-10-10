package com.wuc.lib_base.ext

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat

/**
 * @author: wuc
 * @date: 2024/10/8
 * @desc:
 */

// 获取 Application
@Volatile
lateinit var application: Application
    internal set

// 获取包名
inline val packageName: String get() = application.packageName

inline val packageInfo: PackageInfo
    get() = application.packageManager.getPackageInfo(packageName, 0)

// 获取 App 名字
inline val appName: String
    get() = application.applicationInfo.loadLabel(application.packageManager).toString()

// 获取 App 图标
inline val appIcon: Drawable get() = packageInfo.applicationInfo.loadIcon(application.packageManager)

// 获取 App 版本号
inline val appVersionName: String get() = packageInfo.versionName

// 获取 App 版本码
inline val appVersionCode: Long get() = PackageInfoCompat.getLongVersionCode(packageInfo)

// 判断 App 是否是 Debug 版本
inline val isAppDebug: Boolean get() = application.isAppDebug

inline val Application.isAppDebug: Boolean
    get() = packageManager.getApplicationInfo(packageName, 0).flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

// 判断 App 是否是夜间模式
inline val isAppDarkMode: Boolean
    get() = (application.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES

// 启动 App 详情设置
fun launchAppSettings(): Boolean =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply { data = Uri.fromParts("package", packageName, null) }
        .startForActivity()

// 重启 App
fun relaunchApp(killProcess: Boolean = true) =
    application.packageManager.getLaunchIntentForPackage(packageName)?.let {
        it.addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(it)
        if (killProcess) android.os.Process.killProcess(android.os.Process.myPid())
    }

fun doOnAppStatusChanged(onForeground: ((Activity) -> Unit)? = null, onBackground: ((Activity) -> Unit)? = null) {
    doOnAppStatusChanged(object : OnAppStatusChangedListener {
        override fun onForeground(activity: Activity) {
            onForeground?.invoke(activity)
        }

        override fun onBackground(activity: Activity) {
            onBackground?.invoke(activity)
        }
    })
}

fun doOnAppStatusChanged(listener: OnAppStatusChangedListener) {
    AppInitializer.onAppStatusChangedListener = listener
}

interface OnAppStatusChangedListener {
    fun onForeground(activity: Activity)
    fun onBackground(activity: Activity)
}