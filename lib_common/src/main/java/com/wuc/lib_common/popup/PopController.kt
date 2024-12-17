package com.wuc.lib_common.popup

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import androidx.annotation.FloatRange

/**
 * @author: wuc
 * @date: 2024/12/17
 * @description:
 */
class PopController(private val context: Context, private val popupWindow: PopupWindow) {
    private var layoutResId = 0 // 用于存储布局资源ID
    private var mView: View? = null // 用于存储自定义视图
    var mPopupView: View? = null  // 弹窗布局的根视图
    private var mWindow: Window? = null // 用于调整背景灰度的窗口对象

    // 设置布局资源ID，并更新弹窗内容
    fun setView(layoutResId: Int) {
        mView = null
        this.layoutResId = layoutResId
        installContent()
    }

    // 设置自定义视图，并更新弹窗内容
    fun setView(view: View?) {
        mView = view
        layoutResId = 0
        installContent()
    }

    // 根据当前设置的资源ID或视图，更新弹窗的内容
    private fun installContent() {
        if (layoutResId != 0) {
            mPopupView = LayoutInflater.from(context).inflate(layoutResId, null)
        } else if (mView != null) {
            mPopupView = mView
        }
        detachFromParentView(mPopupView)  // 移除mPopupView的父视图，防止视图重复添加导致的错误
        popupWindow.contentView = mPopupView
    }

    /**
     * 设置弹窗的宽度和高度
     * @param width  宽度
     * @param height 高度
     */
    private fun setWidthAndHeight(width: Int, height: Int) {
        if (width == 0 || height == 0) {
            // 如果宽度或高度未设置，使用WRAP_CONTENT
            popupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
            popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            popupWindow.width = width
            popupWindow.height = height
        }
    }

    /**
     * 设置窗口背景的灰度级别
     * @param level 灰度级别，范围从0.0f（完全透明）到1.0f（完全不透明）
     */
    fun setBackGroundLevel(@FloatRange(from = 0.0, to = 1.0) level: Float) {
        val clampedLevel = level.coerceIn(0.0f, 1.0f) // 确保level值在0.0f到1.0f之间
        mWindow = (context as Activity).window
        val params = mWindow?.attributes
        params?.alpha = clampedLevel
        mWindow?.attributes = params
    }

    /**
     * 设置弹窗的动画样式
     * @param animationStyle 动画资源ID
     */
    private fun setAnimStyle(animationStyle: Int) {
        popupWindow.animationStyle = animationStyle
    }

    /**
     * 设置弹窗外部是否可以触摸
     * @param touchable 是否可触摸
     */
    private fun setOutsideTouchable(touchable: Boolean) {
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000)) // 设置透明背景以支持点击外部关闭
        popupWindow.isOutsideTouchable = touchable // 设置外部可触摸
        popupWindow.isFocusable = touchable // 设置是否可以获取焦点
    }

    // 内部类，用于配置弹窗的参数
    internal class PopupParams(var mContext: Context) {
        var layoutResId = 0 // 布局资源ID
        var mWidth = 0 // 弹窗宽度
        var mHeight = 0 // 弹窗高度
        var isShowBg = false // 是否显示背景灰度
        var isShowAnim = false // 是否显示动画
        var bgLevel = 0f // 背景灰度级别
        var animStyle = 0 // 动画资源ID
        var mView: View? = null // 自定义视图
        var isTouchable = true // 外部是否可触摸

        // 应用配置到PopController
        fun apply(controller: PopController) {
            when {
                mView != null -> {
                    controller.setView(mView)
                }
                layoutResId != 0 -> {
                    controller.setView(layoutResId)
                }
                else -> {
                    throw IllegalArgumentException("PopupView's contentView is null")
                }
            }
            controller.setWidthAndHeight(mWidth, mHeight)
            controller.setOutsideTouchable(isTouchable)
            if (isShowBg) {
                controller.setBackGroundLevel(bgLevel)
            }
            if (isShowAnim) {
                controller.setAnimStyle(animStyle)
            }
        }
    }

    // 从其父视图中移除视图
    private fun detachFromParentView(childView: View?) {
        val viewParent = childView?.parent
        if (viewParent != null && viewParent is ViewGroup) {
            viewParent.removeView(childView)
        }
    }
}