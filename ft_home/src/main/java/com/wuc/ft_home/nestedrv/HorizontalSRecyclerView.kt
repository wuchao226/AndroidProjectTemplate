package com.wuc.ft_home.nestedrv

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * @author: wuc
 * @date: 2024/12/17
 * @description: 在 ViewPager - RecyclerView 中的item里有RecyclerView需要左右滑动时使用
 */
class HorizontalSRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr) {
    //按下时 的X坐标
    private var downX = 0f

    //按下时 的Y坐标
    private var downY = 0f

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        val x = e.x  // 获取当前触摸事件的X坐标
        val y = e.y  // 获取当前触摸事件的Y坐标
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // 在用户按下时记录当前坐标
                downX = x
                downY = y
            }
            MotionEvent.ACTION_MOVE -> {
                // 计算从按下到当前位置的X和Y方向上的位移
                val dx = x - downX
                val dy = y - downY
                // 根据位移判断滑动方向
                val orientation = getOrientation(dx, dy)
                // 获取当前视图在屏幕上的位置
                val location = intArrayOf(0, 0)
                getLocationOnScreen(location)
                when (orientation) {
                    // 如果是向下滑动或向上滑动，不拦截事件，让子视图处理
                    "d", "u" -> parent.requestDisallowInterceptTouchEvent(false)
                    "r" -> {
                        // 如果可以向右滑动，让本视图处理滑动事件
                        if (canScrollHorizontally(-1)) {
                            parent.requestDisallowInterceptTouchEvent(true)
                        } else {
                            // 如果向右滑动到边界，让父视图处理，以便可以滑动到ViewPager的下一个页面
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    "l" -> {
                        // 如果可以向左滑动，让本视图处理滑动事件
                        if (canScrollHorizontally(1)) {
                            parent.requestDisallowInterceptTouchEvent(true)
                        } else {
                            // 如果向左滑动到边界，让父视图处理，以便可以滑动到ViewPager的上一个页面
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                }
            }
        }
        // 调用父类的方法继续处理触摸事件
        return super.dispatchTouchEvent(e)
    }

    /**
     * 根据水平和垂直方向上的位移差来判断滑动的方向。
     * 
     * @param dx 水平方向的位移差，正值表示向右滑动，负值表示向左滑动。
     * @param dy 垂直方向的位移差，正值表示向下滑动，负值表示向上滑动。
     * @return 返回滑动的方向，"r"表示向右，"l"表示向左，"d"表示向下，"u"表示向上。
     */
    private fun getOrientation(dx: Float, dy: Float): String {
        // 判断水平位移和垂直位移的绝对值大小，以确定主要的滑动方向是水平还是垂直。
        return if (Math.abs(dx) > Math.abs(dy)) {
            // 主要方向为水平，根据dx的正负判断是向右还是向左滑动。
            if (dx > 0) "r" else "l"
        } else {
            // 主要方向为垂直，根据dy的正负判断是向下还是向上滑动。
            if (dy > 0) "d" else "u"
        }
    }
}