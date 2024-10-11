package com.wuc.lib_base.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.wuc.lib_base.ext.application
import com.wuc.lib_base.log.WLogUtils
import java.lang.reflect.Field

/**
 * @author: wuc
 * @date: 2024/10/10
 * @desc: 解决 Android 自身的 Bug
 */
object AndroidBugFixUtils {
    /**
     * 修复 InputMethodManager 引起的内存泄漏问题
     * @param activity 当前的 Activity
     *
     * 使用方式：
     * ```
     * override fun onDestroy() {
     *     AndroidBugFixUtils().fixSoftInputLeaks(this)
     *     super.onDestroy()
     * }
     * ```
     */
    @SuppressLint("PrivateApi")
    fun fixSoftInputLeaks(activity: Activity) {
        // 获取 InputMethodManager 实例
        val imm = application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 需要检查的可能导致内存泄漏的字段名
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        
        // 遍历这些字段名
        for (leakView in leakViews) {
            try {
                // 通过反射获取 InputMethodManager 类中的指定字段
                val leakViewField: Field = InputMethodManager::class.java.getDeclaredField(leakView) ?: continue
                // 如果字段不可访问，则设置为可访问
                if (!leakViewField.isAccessible) leakViewField.isAccessible = true
                // 获取字段的值
                val view: Any? = leakViewField.get(imm)
                // 如果字段值不是 View 类型，则跳过
                if (view !is View) continue
                // 检查该 View 的根视图是否与当前 Activity 的根视图相同
                if (view.rootView == activity.window.decorView.rootView) {
                    // 如果相同，则将该字段值设置为 null，解除引用
                    leakViewField.set(imm, null)
                }
            } catch (t: Throwable) {
                // 捕获异常并记录日志，便于调试
                WLogUtils.et("fixSoftInputLeaks", "Failed to fix leak for field: $leakView", t)
            }
        }
    }
}