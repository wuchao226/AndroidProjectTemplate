package com.wuc.lib_common.widget.layout

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * @author: wuc
 * @date: 2024/10/27
 * @description: 支持监听渐变的 CollapsingToolbarLayout
 */
class NestCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyleAttr) {

    /** 渐变监听 */
    private var listener: OnScrimsListener? = null

    /** 当前渐变状态 */
    private var scrimsShownStatus: Boolean = false
    /**
     * 重写 setScrimsShown 方法，以支持动画效果的渐变显示。
     * @param shown 是否显示遮罩层，true 表示显示，通常在工具栏折叠时使用。false 表示不显示，通常在工具栏展开时使用。
     * @param animate 是否使用动画效果。
     */
    override fun setScrimsShown(shown: Boolean, animate: Boolean) {
        // 调用父类的 setScrimsShown 方法，强制使用动画效果
        super.setScrimsShown(shown, true)
        // 检查渐变状态是否已经是目标状态
        if (scrimsShownStatus == shown) {
            // 如果状态未改变，则直接返回，不进行后续操作
            return
        }
        // 更新当前渐变状态
        scrimsShownStatus = shown
        // 如果存在监听器，则调用监听器的 onScrimsStateChange 方法，通知状态变化
        listener?.onScrimsStateChange(this, scrimsShownStatus)
    }

    /**
     * 获取当前的渐变状态
     */
    fun isScrimsShown(): Boolean {
        return scrimsShownStatus
    }

    /**
     * 设置CollapsingToolbarLayout渐变监听
     */
    fun setOnScrimsListener(listener: OnScrimsListener) {
        this.listener = listener
    }

    /**
     * CollapsingToolbarLayout渐变监听器
     */
    interface OnScrimsListener {
        /**
         * 渐变状态变化
         *
         * @param shown    渐变开关
         */
        fun onScrimsStateChange(layout: NestCollapsingToolbarLayout, shown: Boolean)
    }
}