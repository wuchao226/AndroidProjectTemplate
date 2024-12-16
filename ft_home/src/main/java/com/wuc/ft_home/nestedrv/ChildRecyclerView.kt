package com.wuc.ft_home.nestedrv

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wuc.ft_home.nestedrv.NestedOverScroller.invokeCurrentVelocity
import kotlin.math.abs


/**
 * @author: wuc
 * @date: 2024/12/14
 * @description: 内层的RecyclerView
 */
class ChildRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mParentRecyclerView: ParentRecyclerView? = null

    /**
     * fling时的加速度
     */
    private var mVelocity = 0

    private var mLastInterceptX = 0

    private var mLastInterceptY = 0

    init {
        this.overScrollMode = View.OVER_SCROLL_NEVER
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 当滑动停止时
                    dispatchParentFling()
                }
            }
        })
    }

    /**
     * 将剩余的加速度传递给父RecyclerView进行处理。
     * 此方法首先确保父RecyclerView已经被正确初始化。
     * 如果当前子RecyclerView已滚动到顶部并且存在剩余的加速度，
     * 则尝试将这些加速度平滑地传递给父RecyclerView。
     */
    private fun dispatchParentFling() {
        // 确保父RecyclerView已经被初始化
        ensureParentRecyclerView()
        // 检查是否满足将加速度传递给父RecyclerView的条件
        if (mParentRecyclerView != null && isScrollToTop() && mVelocity != 0) {
            // 从NestedOverScroller中获取当前的滚动速度
            var velocityY = invokeCurrentVelocity(this)
            // 如果当前速度非常小（小于或等于2.0E-5f），则使用原始速度的一半
            if (abs(velocityY.toDouble()) <= 2.0E-5f) {
                velocityY = mVelocity.toFloat() * 0.5f
            } else {
                // 否则，将速度减少35%，以便更平滑地减速
                velocityY *= 0.65f
            }
            // 调用父RecyclerView的fling方法，传递处理过的速度
            mParentRecyclerView?.fling(0, velocityY.toInt())
            // 重置当前RecyclerView的速度
            mVelocity = 0
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // 当触摸事件为按下动作时，重置滑动速度为0
        if (ev.action == MotionEvent.ACTION_DOWN) {
            mVelocity = 0
        }

        // 获取当前触摸事件的X和Y坐标
        val x = ev.rawX.toInt()
        val y = ev.rawY.toInt()

        // 如果当前动作不是移动动作，则更新最后拦截的X和Y坐标
        if (ev.action != MotionEvent.ACTION_MOVE) {
            mLastInterceptX = x
            mLastInterceptY = y
        }

        // 计算X和Y方向上的位移差
        val deltaX = x - mLastInterceptX
        val deltaY = y - mLastInterceptY

        // 如果当前RecyclerView已滚动到顶部，并且Y方向的位移大于X方向的位移，并且存在父视图
        if (isScrollToTop() && abs(deltaX.toDouble()) <= abs(deltaY.toDouble()) && parent != null) {
            // 子容器滚动到顶部，继续向上滑动，此时父容器需要继续拦截事件。与父容器 onInterceptTouchEvent 对应
            // 当子容器已滚动到顶部且用户尝试继续向上滑动时，请求父容器不要拦截触摸事件，允许子容器处理
            parent.requestDisallowInterceptTouchEvent(false)
        }

        // 调用父类的dispatchTouchEvent方法，继续传递触摸事件
        return super.dispatchTouchEvent(ev)
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        // 检查视图是否已经附加到窗口，如果没有，则不执行fling操作
        if (!isAttachedToWindow) return false
        // 调用父类的fling方法，尝试进行fling操作
        val fling = super.fling(velocityX, velocityY)
        // 如果fling操作未成功或者Y轴速度为正（向下滑动），则将mVelocity设置为0，否则保留Y轴速度
        mVelocity = if (!fling || velocityY >= 0) {
            0
        } else {
            velocityY
        }
        // 返回fling操作的结果
        return fling
    }

    // 检查是否可以向上滚动，即是否已滚动到顶部
    fun isScrollToTop(): Boolean {
        return !canScrollVertically(-1)
    }

    // 检查是否可以向下滚动，即是否已滚动到底部
    fun isScrollToBottom(): Boolean {
        return !canScrollVertically(1)
    }

    // 确保mParentRecyclerView引用指向正确的父RecyclerView
    private fun ensureParentRecyclerView() {
        // 如果mParentRecyclerView为空，则从当前视图的父视图开始向上查找，直到找到类型为ParentRecyclerView的视图
        if (mParentRecyclerView == null) {
            var parentView = parent
            while (parentView !is ParentRecyclerView) {
                parentView = parentView.parent
            }
            // 将找到的ParentRecyclerView赋值给mParentRecyclerView
            mParentRecyclerView = parentView
        }
    }
}