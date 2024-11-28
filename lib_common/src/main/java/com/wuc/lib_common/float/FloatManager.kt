package com.wuc.lib_common.float

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.core.view.contains
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.wuc.lib_common.widget.view.BaseFloatView

/**
 * 浮动管理器，用于管理浮动视图的显示和隐藏。
 */
@SuppressLint("StaticFieldLeak")
object FloatManager {
    private lateinit var mContentView: FrameLayout // 主内容视图
    private var mActivity: Activity? = null // 当前关联的Activity
    private var mFloatView: BaseFloatView? = null // 浮动视图
    private var mIsShowing: Boolean = false // 标记是否正在显示浮动视图

    /**
     * 初始化浮动管理器与Activity的关联。
     */
    fun with(activity: Activity): FloatManager {
        mContentView = activity.window.decorView.findViewById(android.R.id.content) as FrameLayout
        mActivity = activity
        addLifecycle(mActivity)
        return this
    }

    /**
     * 添加浮动视图。
     */
    fun add(floatView: BaseFloatView): FloatManager {
        if (mIsShowing) return this
        mFloatView = floatView
        return this
    }

    /**
     * 设置浮动视图的点击监听器。
     */
    fun setClick(listener: BaseFloatView.OnFloatClickListener): FloatManager {
        mFloatView?.setOnFloatClickListener(listener)
        return this
    }

    /**
     * 显示浮动视图。
     */
    fun show() {
        checkParams()
        if (!mIsShowing && mFloatView != null && !mContentView.contains(mFloatView!!)) {
            mContentView.removeView(mFloatView)
            mContentView.addView(mFloatView)
            // 将浮动视图置于顶层
            mFloatView?.bringToFront()
            mIsShowing = true
        }
    }

    /**
     * 检查必要的参数是否已经设置。
     */
    private fun checkParams() {
        if (mActivity == null) {
            throw NullPointerException("Activity must be set before calling show()")
        }
        if (mFloatView == null) {
            throw NullPointerException("FloatView must be set before calling show()")
        }
    }

    private var mLifecycleEventObserver: LifecycleEventObserver? = null

    /**
     * 添加生命周期观察者，用于在Activity销毁时自动隐藏浮动视图。
     */
    private fun addLifecycle(activity: Activity?) {
        if (activity is ComponentActivity) {
            val lifecycleEventObserver = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    hide()
                }
            }
            activity.lifecycle.addObserver(lifecycleEventObserver)
            mLifecycleEventObserver = lifecycleEventObserver
        }
    }

    /**
     * 隐藏浮动视图，并清理资源。
     */
    fun hide() {
        if (mIsShowing && ::mContentView.isInitialized && mFloatView != null && mContentView.contains(mFloatView!!)) {
            mContentView.removeView(mFloatView)
            mFloatView?.release()
            mFloatView = null
            mIsShowing = false
        }
        mLifecycleEventObserver?.let { (mActivity as? ComponentActivity)?.lifecycle?.removeObserver(it) }
        mActivity = null
    }
}