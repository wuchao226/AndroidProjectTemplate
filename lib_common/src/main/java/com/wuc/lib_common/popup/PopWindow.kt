package com.wuc.lib_common.popup

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.view.ContextThemeWrapper
import com.wuc.lib_base.log.WLogUtils

/**
 * @author: wuc
 * @date: 2024/12/17
 * @description:
 * Android 封装一个通用的PopupWindow：https://mp.weixin.qq.com/s?__biz=Mzg4Mjc1ODA1Mw==&mid=2247483824&idx=1&sn=7a52e58cc3b9b929603e35fbd0165072&chksm=cf50997af827106cf739395381bd1001f2bbe8f387fd18e597694a428f0cf041af9189393c97&scene=178&cur_album_id=2353350584667570178#rd
 */
class PopWindow private constructor(val context: Context) : PopupWindow() {
    companion object {
        const val CENTER_TOP = 0 //正上方
        const val LEFT_TOP = 1 //左上方
        const val RIGHT_TOP = 2 //右上方
        const val CENTER_BOTTOM = 3 //正下方
        const val LEFT_BOTTOM = 4 //左下方
        const val RIGHT_BOTTOM = 5 //右下方
        const val CENTER_RIGHT = 6 //右侧
        const val CENTER_LEFT = 7 //左侧

        /**
         * 测量视图的宽度和高度。
         * @param view 需要测量的视图，可以为null。
         */
        fun measureWidthAndHeight(view: View?) {
            // 创建一个未指定大小的宽度测量规格
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            // 创建一个未指定大小的高度测量规格
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            // 对视图进行测量，获取宽度和高度
            view?.measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    // 创建一个PopController实例，用于管理PopupWindow的配置和行为
    val controller: PopController = PopController(context, this)

    /**
     * 在目标View的上方显示弹窗
     * @param target  目标View
     * @param gravity 弹窗相对于目标视图的位置，可以是正上方、左上方或右上方。
     * @param wExtra x轴微调使用 >0时向右移，<0时向左移
     * @param hExtra y轴微调使用 >0时向下移, <0时向上移
     */
    fun showOnTargetTop(
        target: View,
        gravity: Int = CENTER_TOP,
        wExtra: Int = 0,
        hExtra: Int = 0,
    ) {
        var xOff = 0  // 水平方向的偏移量
        when (gravity) {
            CENTER_TOP -> {
                // 如果位置是正上方，计算使弹窗水平居中的偏移量
                xOff = -(width - target.measuredWidth) / 2
            }

            LEFT_TOP -> {
                // 如果位置是左上方，计算使弹窗左对齐目标视图的偏移量
                xOff = -(width - target.measuredWidth / 2)
            }

            RIGHT_TOP -> {
                // 如果位置是右上方，计算使弹窗右对齐目标视图的偏移量
                xOff = target.measuredWidth / 2
            }
        }
        // 显示弹窗，考虑额外的水平和垂直偏移
        showAsDropDown(target, xOff + wExtra, -(height + target.measuredHeight) + hExtra)
    }

    /**
     * 在当前View的下方展示
     * @param target  目标View
     * @param gravity
     * @param wExtra x轴微调使用 >0时向右移，<0时向左移
     * @param hExtra y轴微调使用 >0时向下移, <0时向上移
     */
    fun showOnTargetBottom(
        target: View,
        gravity: Int = CENTER_BOTTOM,
        wExtra: Int = 0,
        hExtra: Int = 0,
    ) {
        var xOff = 0
        when (gravity) {
            CENTER_BOTTOM -> {
                //正下方
                xOff = -(width - target.measuredWidth) / 2
            }

            LEFT_BOTTOM -> {
                //左下方
                xOff = -(width - target.measuredWidth / 2)
            }

            RIGHT_BOTTOM -> {
                //右下方
                xOff = target.measuredWidth / 2
            }
        }
        showAsDropDown(target, xOff + wExtra, hExtra)
    }

    /**
     * 在当前View的右侧展示
     * @param target  目标View
     * @param gravity
     * @param wExtra x轴微调使用 >0时向右移，<0时向左移
     * @param hExtra y轴微调使用 >0时向下移, <0时向上移
     */
    fun showOnTargetRight(
        target: View,
        gravity: Int = CENTER_RIGHT,
        wExtra: Int = 0,
        hExtra: Int = 0,
    ) {
        var hOff = 0
        when (gravity) {
            CENTER_RIGHT -> {
                //右侧
                hOff = -(height + target.height) / 2
            }
        }
        showAsDropDown(target, target.measuredWidth + wExtra, hOff + hExtra)
    }

    /**
     * 在当前View的左侧展示
     * @param target  目标View
     * @param gravity
     * @param wExtra x轴微调使用 >0时向右移，<0时向左移
     * @param hExtra y轴微调使用 >0时向下移, <0时向上移
     */
    fun showOnTargetLeft(
        target: View,
        gravity: Int = CENTER_LEFT,
        wExtra: Int = 0,
        hExtra: Int = 0,
    ) {
        var hOff = 0
        when (gravity) {
            CENTER_LEFT -> {
                //左侧
                hOff = -(height + target.height) / 2
            }
        }
        showAsDropDown(target, -width + wExtra, hOff + hExtra)
    }

    /**
     * 显示弹窗相对于指定视图的下方位置。
     * 这个方法是一个重载方法，它调用了另一个重载版本的showAsDropDown，允许额外的回调。
     * @param anchor 锚点视图，弹窗将显示在此视图的下方。
     * @param xoff 弹窗相对于锚点的X轴偏移量，正值向右偏移，负值向左偏移。
     * @param yoff 弹窗相对于锚点的Y轴偏移量，正值向下偏移，负值向上偏移。
     */
    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        showAsDropDown(anchor, xoff, yoff, null)
    }

    /**
     * 显示弹窗在指定视图的下方。
     * @param anchor 锚点视图，弹窗将显示在此视图的下方。
     * @param xoff 弹窗相对于锚点的X轴偏移量，正值向右偏移，负值向左偏移。
     * @param yoff 弹窗相对于锚点的Y轴偏移量，正值向下偏移，负值向上偏移。
     * @param block 异常回调函数，当显示弹窗过程中发生异常时执行。
     */
    fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, block: (() -> Unit)? = null) {
        try {
            // 检查锚点所在的活动是否正在结束，如果不是，则显示弹窗
            val activity = anchor?.context?.getActivity()
            if (activity?.isFinishing == false) {
                super.showAsDropDown(anchor, xoff, yoff)
            }
        } catch (ex: Throwable) {
            // 打印异常堆栈信息
            ex.printStackTrace()
            // 如果提供了异常回调，则调用该回调
            block?.invoke()
        }
    }
    /**
     * 在指定位置显示弹窗。
     * 这个方法是一个重载方法，它调用了另一个重载版本的showAtLocation，允许额外的回调。
     * @param parent 父视图，弹窗将显示在此视图的上下文中。
     * @param gravity 弹窗显示时的位置关系，例如Gravity.CENTER等。
     * @param x 弹窗相对于父视图的X轴偏移量。
     * @param y 弹窗相对于父视图的Y轴偏移量。
     */
    override fun showAtLocation(
        parent: View?,
        gravity: Int,
        x: Int,
        y: Int,
    ) {
        showAtLocation(parent, gravity, x, y, null)
    }

    /**
     * 在指定位置显示弹窗，并提供异常处理机制。
     * @param parent 父视图，弹窗将显示在此视图的上下文中。
     * @param gravity 弹窗显示时的位置关系，例如Gravity.CENTER等。
     * @param x 弹窗相对于父视图的X轴偏移量。
     * @param y 弹窗相对于父视图的Y轴偏移量。
     * @param block 可选的异常处理回调函数，当显示弹窗过程中发生异常时执行。
     */
    fun showAtLocation(
        parent: View?,
        gravity: Int,
        x: Int,
        y: Int,
        block: (() -> Unit)? = null,
    ) {
        try {
            // 检查父视图的上下文是否为Activity且未结束，确保在有效的Activity上显示弹窗
            val activity = parent?.context?.getActivity()
            if (activity?.isFinishing == false) {
                super.showAtLocation(parent, gravity, x, y)  // 调用父类方法显示弹窗
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()  // 打印异常堆栈信息
            block?.invoke()  // 如果提供了异常回调，则调用该回调
        }
    }

    /**
     * 获取弹窗的宽度。
     * 如果弹窗视图已经测量过，返回测量的宽度；如果未测量，返回0。
     * @return 弹窗的宽度或0
     */
    override fun getWidth(): Int {
        return controller.mPopupView?.measuredWidth ?: 0
    }

    /**
     * 获取弹窗的高度。
     * 如果弹窗视图已经测量过，返回测量的高度；如果未测量，返回0。
     * @return 弹窗的高度或0
     */
    override fun getHeight(): Int {
        return controller.mPopupView?.measuredHeight ?: 0
    }

    /**
     * 关闭弹窗。
     * 在关闭前检查当前上下文是否为有效的Activity且未结束，且弹窗当前处于显示状态。
     * 关闭后将背景灰度设置为完全不透明。
     */
    override fun dismiss() {
        try {
            // 检查上下文是否为有效的Activity且未结束，且弹窗是否正在显示
            if ((context as? Activity)?.isFinishing == false && this.isShowing) {
                super.dismiss()  // 调用父类方法关闭弹窗
            }
            controller.setBackGroundLevel(1.0f)  // 关闭后将背景灰度设置为完全不透明
        } catch (ex: Throwable) {
            ex.printStackTrace()  // 打印异常信息
            controller.setBackGroundLevel(1.0f)  // 异常情况下也确保背景灰度设置为完全不透明
        }
    }

    interface ViewInterface {
        /**
         * 为PopupWindow提供一个接口，用于获取并配置其内部的视图。
         * 
         * @param view 传入的是PopupWindow中用作内容的根视图。
         * @param layoutResId 用于指定PopupWindow内部布局的资源ID。如果调用方未传入有效的资源ID，默认为0。
         * @param pop 引用调用此方法的PopupWindow实例，允许在接口实现中对PopupWindow进行进一步的配置或操作。
         */
        fun getChildView(view: View, layoutResId: Int, pop: PopupWindow)
    }

    class Builder(context: Context) {

        // 初始化PopupParams对象，用于存储和管理PopupWindow的配置参数
        private val params: PopController.PopupParams = PopController.PopupParams(context)
        // 定义一个ViewInterface类型的可空变量listener，用于后续设置自定义视图的回调接口
        private var listener: ViewInterface? = null

        /**
         * @param layoutResId 设置PopupWindow 布局ID
         */
        fun setView(layoutResId: Int): Builder {
            params.mView = null
            params.layoutResId = layoutResId
            return this
        }

        /**
         * @param view 设置PopupWindow布局
         */
        fun setView(view: View): Builder {
            params.mView = view
            params.layoutResId = 0
            return this
        }

        /**
         * 设置子View
         * @param listener ViewInterface
         */
        fun setChildrenView(listener: ViewInterface?): Builder {
            this.listener = listener
            return this
        }

        /**
         * 设置宽度和高度 如果不设置 默认是wrap_content
         * @param width 宽
         */
        fun setWidthAndHeight(width: Int, height: Int): Builder {
            params.mWidth = width
            params.mHeight = height
            return this
        }

        /**
         * 设置背景灰色程度
         * @param level 0.0f-1.0f
         */
        fun setBackGroundLevel(level: Float): Builder {
            params.isShowBg = true
            params.bgLevel = level
            return this
        }

        /**
         * 是否可点击Outside消失
         * @param touchable 是否可点击
         */
        fun setOutsideTouchable(touchable: Boolean): Builder {
            params.isTouchable = touchable
            return this
        }

        /**
         * 设置动画
         */
        fun setAnimStyle(animStyle: Int): Builder {
            params.isShowAnim = true
            params.animStyle = animStyle
            return this
        }

        fun create(): PopWindow {
            // 创建一个PopWindow实例，传入上下文
            val popupWindow = PopWindow(params.mContext)
            // 应用配置参数到PopWindow的控制器
            params.apply(popupWindow.controller)
            // 获取PopWindow中的视图
            val popView = popupWindow.controller.mPopupView
            // 如果设置了listener且popView不为空，则调用getChildView方法
            if (listener != null && popView != null) {
                listener?.getChildView(popView, params.layoutResId, popupWindow)
            }
            // 测量popView的宽度和高度
            measureWidthAndHeight(popView)
            // 返回构建好的PopWindow对象
            return popupWindow
        }

    }

    fun Context.getActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }
}