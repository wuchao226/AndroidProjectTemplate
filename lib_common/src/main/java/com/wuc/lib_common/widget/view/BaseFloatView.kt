package com.wuc.lib_common.widget.view

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import kotlin.math.abs
import kotlin.math.roundToInt

// 吸边的方式
sealed class FloatAdsorbType {
    // 垂直方向吸边
    data object AdsorbVertical : FloatAdsorbType()

    // 水平方向吸边
    data object AdsorbHorizontal : FloatAdsorbType()
}

/**
 * @author: wuc
 * @date: 2024/11/27
 * @description: 随意拖拽+自动吸边
 */
abstract class BaseFloatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener {
    private var mViewWidth = 0
    private var mViewHeight = 0

    // toolbar默认高度
    private var mToolBarHeight = dp2px(56F)

    // 默认吸边需要的拖拽距离为屏幕的一半
    private var mDragDistance = 0.5

    private val decelerateInterpolator = DecelerateInterpolator()

    init {
        initView()
    }

    fun initView() {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = mToolBarHeight
        }
        addView(getChildView())
        setOnTouchListener(this)

        post {
            // 获取一下view宽高，方便后面计算，省的bottom-top麻烦
            mViewWidth = width
            mViewHeight = height
        }
    }

    /**
     * 获取子view
     */
    protected abstract fun getChildView(): View

    /**
     * 是否可以拖拽
     */
    protected abstract fun getIsCanDrag(): Boolean

    /**
     * 吸边的方式
     */
    protected abstract fun getAdsorbType(): FloatAdsorbType

    /**
     * 多久自动缩一半
     * 默认：3000，单位：毫秒，小于等于0则不自动缩
     */
    open fun getAutoAdsorbTimeMillis(): Long {
        return 3000
    }

    /**
     * 点击事件
     */
    protected var mOnFloatClickListener: OnFloatClickListener? = null

    interface OnFloatClickListener {
        fun onClick(view: View)
    }

    fun setOnFloatClickListener(listener: OnFloatClickListener) {
        mOnFloatClickListener = listener
    }

    /**
     * 设置吸边需要的拖拽距离，默认半屏修改吸边方向，取值0-1
     */
    fun setDragDistance(@FloatRange(from = 0.0, to = 1.0) distance: Double) {
        require(distance in 0.0..1.0) { "Drag distance must be between 0.0 and 1.0" }
        mDragDistance = distance
    }

    private var mIsInside = false

    private var mHandler = Handler(Looper.getMainLooper())

    /**
     * 自动缩到屏幕内一半，目前只支持左右（水平方向），垂直防线改改参数就行
     */
    private val mRunnable = Runnable {
        // 计算目标X坐标。如果当前视图的X坐标大于屏幕宽度的一半，则目标X坐标设置为屏幕宽度减去视图宽度的一半；否则，目标X坐标设置为负的视图宽度的一半。
        val targetX = if (x > getScreenWidth() / 2) getScreenWidth() - mViewWidth / 2 else -width / 2
        // 执行动画，将视图移动到计算出的目标X坐标，动画时间为300毫秒，透明度设置为0.5。
        animate().setInterpolator(decelerateInterpolator).setDuration(300).alpha(0.5f).x(targetX.toFloat()).start()
        // 设置标志位，表示视图已经移动到屏幕内部。
        mIsInside = true
    }

    private var mDownX = 0F  // 记录触摸事件ACTION_DOWN时的X坐标
    private var mDownY = 0F  // 记录触摸事件ACTION_DOWN时的Y坐标
    private var mFirstY: Int = 0  // 记录第一次触摸时在屏幕上的Y坐标
    private var mFirstX: Int = 0  // 记录第一次触摸时在屏幕上的X坐标
    private var isMove = false  // 标记是否有移动发生

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x = event.x  // 获取触摸事件的X坐标
        val y = event.y  // 获取触摸事件的Y坐标
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                // 记录第一次在屏幕上坐标，用于计算初始位置
                // 将触摸事件的原始Y坐标四舍五入后赋值给mFirstY
                mFirstY = event.rawY.roundToInt()
                // 将触摸事件的原始X坐标四舍五入后赋值给mFirstX
                mFirstX = event.rawX.roundToInt()

                mHandler.removeCallbacksAndMessages(mRunnable)

                resetStatus()
                // 确保在ACTION_DOWN时重置isMove
                isMove = false
            }

            MotionEvent.ACTION_MOVE -> {
                // 设置一个阈值来判断是否为拖动
                if (abs(x - mDownX) > 10 || abs(y - mDownY) > 10) {
                    isMove = true
                    // 根据触摸移动的垂直距离，调整视图的顶部和底部位置
                    offsetTopAndBottom((y - mDownY).toInt())
                    // 根据触摸移动的水平距离，调整视图的左侧和右侧位置
                    offsetLeftAndRight((x - mDownX).toInt())
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isMove) {
                    when (getAdsorbType()) {
                        is FloatAdsorbType.AdsorbVertical -> {
                            adsorbTopAndBottom(event)
                        }

                        is FloatAdsorbType.AdsorbHorizontal -> {
                            adsorbLeftAndRight(event)
                        }
                    }
                } else {
                    mOnFloatClickListener?.onClick(v)
                }
                isMove = false

                if (getAutoAdsorbTimeMillis() > 0) {
                    mHandler.postDelayed(mRunnable, getAutoAdsorbTimeMillis())
                }
            }
        }
        return getIsCanDrag()
    }

    /**
     * 重置视图的状态，如果视图已经缩到屏幕内部，根据当前位置决定动画移动到屏幕左边或右边。
     */
    private fun resetStatus() {
        if (mIsInside) {
            if (x > getScreenWidth() / 2) {
                // 如果视图中心点在屏幕右半边，执行动画将视图移动到屏幕右边
                animate().setInterpolator(decelerateInterpolator).setDuration(300).alpha(1f).x((getScreenWidth() - mViewWidth).toFloat()).start()
            } else {
                // 如果视图中心点在屏幕左半边，执行动画将视图移动到屏幕左边
                animate().setInterpolator(decelerateInterpolator).setDuration(300).alpha(1f).x(0F).start()
            }
            // 设置标志位，表示视图不再处于屏幕内部状态
            mIsInside = false
        }
    }

    /**
     * 上下吸边
     */
    private fun adsorbTopAndBottom(event: MotionEvent) {
        if (isOriginalFromTop()) {
            // 上半屏
            val centerY = mViewHeight / 2 + abs(event.rawY - mFirstY)
            if (centerY < getAdsorbHeight()) {
                //滑动距离<半屏=吸顶
                val topY = 0f + mToolBarHeight
                animate().setInterpolator(decelerateInterpolator).setDuration(300).y(topY).start()
            } else {
                //滑动距离>半屏=吸底
                val bottomY = getContentHeight() - mViewHeight
                animate().setInterpolator(decelerateInterpolator).setDuration(300).y(bottomY.toFloat()).start()
            }
        } else {
            // 下半屏
            val centerY = mViewHeight / 2 + abs(event.rawY - mFirstY)
            if (centerY < getAdsorbHeight()) {
                //滑动距离<半屏=吸底
                val bottomY = getContentHeight() - mViewHeight
                animate().setInterpolator(decelerateInterpolator).setDuration(300).y(bottomY.toFloat()).start()
            } else {
                //滑动距离>半屏=吸顶
                val topY = 0f + mToolBarHeight
                animate().setInterpolator(decelerateInterpolator).setDuration(300).y(topY).start()
            }
        }
        resetHorizontal(event)
    }

    /**
     * 上下拖拽时，如果横向拖拽也超出屏幕，则上下吸边时左右也吸边
     */
    private fun resetHorizontal(event: MotionEvent) {
        // 检查横向位置，如果视图的左边缘小于视图宽度，即视图部分或完全在屏幕外左侧
        if (event.rawX < mViewWidth) {
            // 将视图动画移动到屏幕最左侧
            animate().setInterpolator(decelerateInterpolator).setDuration(300).x(0F).start()
        } 
        // 检查横向位置，如果视图的右边缘超出屏幕宽度减去视图宽度，即视图部分或完全在屏幕外右侧
        else if (event.rawX > getScreenWidth() - mViewWidth) {
            // 将视图动画移动到屏幕最右侧
            animate().setInterpolator(decelerateInterpolator).setDuration(300).x(getScreenWidth().toFloat() - mViewWidth).start()
        }
    }

    /**
     * 左右吸边
     */
    private fun adsorbLeftAndRight(event: MotionEvent) {
        if (isOriginalFromLeft()) {
            // 左半屏
            val centerX = mViewWidth / 2 + abs(event.rawX - mFirstX)
            if (centerX < getAdsorbWidth()) {
                //滑动距离<半屏=吸左
                val leftX = 0f
                animate().setInterpolator(decelerateInterpolator).setDuration(300).x(leftX).start()
            } else {
                //滑动距离>半屏=吸右
                val rightX = getScreenWidth() - mViewWidth
                animate().setInterpolator(decelerateInterpolator).setDuration(300).x(rightX.toFloat()).start()
            }
        } else {
            // 右半屏
            val centerX = mViewWidth / 2 + abs(event.rawX - mFirstX)
            if (centerX < getAdsorbWidth()) {
                //滑动距离<半屏=吸右
                val rightX = getScreenWidth() - mViewWidth
                animate().setInterpolator(decelerateInterpolator).setDuration(300).x(rightX.toFloat()).start()
            } else {
                //滑动距离>半屏=吸左
                val leftX = 0f
                animate().setInterpolator(decelerateInterpolator).setDuration(300).x(leftX).start()
            }
        }
        resetVertical(event)
    }

    /**
     * 左右拖拽时，如果纵向拖拽也超出屏幕，则左右吸边时上下也吸边
     */
    private fun resetVertical(event: MotionEvent) {
        // 当触摸的Y坐标小于视图高度时，将视图动画移动到屏幕顶部，考虑工具栏高度
        if (event.rawY < mViewHeight) {
            animate().setInterpolator(decelerateInterpolator).setDuration(300).y(0F + mToolBarHeight).start()
        } 
        // 当触摸的Y坐标大于屏幕内容高度减去视图高度时，将视图动画移动到屏幕底部
        else if (event.rawY > getContentHeight() - mViewHeight) {
            animate().setInterpolator(decelerateInterpolator).setDuration(300).y(getContentHeight().toFloat() - mViewHeight).start()
        }
    }

    /**
     * 是否缩到屏幕内
     */
    fun isInside(): Boolean {
        return mIsInside
    }

    /**
     * 初始位置是否在顶部
     */
    private fun isOriginalFromTop(): Boolean {
        return mFirstY < getScreenHeight() / 2
    }

    /**
     * 初始位置是否在左边
     */
    private fun isOriginalFromLeft(): Boolean {
        return mFirstX < getScreenWidth() / 2
    }

    /**
     * 获取上下吸边时需要拖拽的距离
     */
    private fun getAdsorbHeight(): Double {
        return getScreenHeight() * mDragDistance
    }

    /**
     * 获取左右吸边时需要拖拽的距离
     */
    private fun getAdsorbWidth(): Double {
        return getScreenWidth() * mDragDistance
    }

    /**
     * dp2px
     */
    private fun dp2px(dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    /**
     * 获取屏幕高度
     */
    private fun getScreenHeight(): Int {
        val dm = DisplayMetrics()
        (context as? Activity)?.windowManager?.defaultDisplay?.getMetrics(dm)
        return dm.heightPixels
    }

    /**
     * 获取屏幕宽度
     */
    private fun getScreenWidth(): Int {
        val dm = DisplayMetrics()
        (context as? Activity)?.windowManager?.defaultDisplay?.getMetrics(dm)
        return dm.widthPixels
    }

    /**
     * 获取页面内容区高度
     */
    private fun getContentHeight(): Int {
        var height = 0
        val view = (context as? Activity)?.window?.decorView?.findViewById<FrameLayout>(android.R.id.content)
        view?.let {
            height = view.bottom
        }
        return height
    }

    /**
     * 回收
     */
    fun release() {
        // do something
    }
}