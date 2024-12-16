package com.wuc.ft_home.nestedrv

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


/**
 * @author: wuc
 * @date: 2024/12/14
 * @description: 外层的RecyclerView
 */
class ParentRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    /**
     * 触摸灵敏度阈值
     * ViewConfiguration.get(getContext()).scaledTouchSlop
     * 获取当前设备的触摸灵敏度阈值
     */
    private val mTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop

    /**
     * fling时的加速度
     */
    // 当前滑动事件的速度，用于fling操作的速度跟踪
    private var mVelocity = 0

    // 上一次触摸事件的Y坐标，用于计算滑动距离和方向
    private var mLastTouchY = 0f

    // 上一次拦截触摸事件的X坐标，用于处理滑动冲突
    private var mLastInterceptX = 0
    // 上一次拦截触摸事件的Y坐标，用于处理滑动冲突
    private var mLastInterceptY = 0


    /**
     * 用于追踪滑动速度的对象
     * VelocityTracker 通过记录在一定时间内的滑动距离来计算滑动速度
     * VelocityTracker 可以用来追踪滑动速度，并且可以将滑动速度传递给子容器
     */
    private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()

    /**
     * 最大的滑动速度
     * 该值越大，滑动速度越快
     */
    private var mMaximumFlingVelocity = 0

    /**
     * 最小的滑动速度
     * 该值越小，滑动速度越慢
     */
    private var mMinimumFlingVelocity = 0

    /**
     * 子容器是否消耗了滑动事件
     */
    private var childConsumeTouch = false

    /**
     * 子容器消耗的滑动距离
     */
    private var childConsumeDistance = 0

    init {
        this.overScrollMode = View.OVER_SCROLL_NEVER
        // 阻止子View的焦点事件,避免滑动过程中,子View因为焦点事件,而导致滑动卡顿
        // 1. FOCUS_BEFORE_DESCENDANTS:ViewGroup 会优先获得焦点,barrier为最前面的view
        // 2. FOCUS_AFTER_DESCENDANTS:ViewGroup 只有在子view不需要焦点时,才会获得焦点
        // 3. FOCUS_BLOCK_DESCENDANTS:ViewGroup 会屏蔽任何子view的焦点事件
        this.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        // 获取当前视图配置
        val configuration = ViewConfiguration.get(getContext())
        // 设置最大滑动速度，这是用户在快速滑动时能达到的最大速度
        mMaximumFlingVelocity = configuration.scaledMaximumFlingVelocity
        // 设置最小滑动速度，这是用户在慢速滑动时的最小速度
        mMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 当滑动停止时
                    dispatchChildFling()
                }
            }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (isChildConsumeTouch(event)) {
            // 子容器如果消费了触摸事件，后续父容器就无法再拦截事件
            // 在必要的时候，子容器需调用 requestDisallowInterceptTouchEvent(false) 来允许父容器继续拦截事件
            return false;
        }
        // 子容器不消费触摸事件，父容器按正常流程处理
        return super.onInterceptTouchEvent(event)
    }

    /**
     * 判断子容器是否消费触摸事件。
     * @param event 触摸事件
     * @return 如果子容器消费了触摸事件，则返回true；否则返回false。
     */
    private fun isChildConsumeTouch(event: MotionEvent): Boolean {
        // 获取触摸事件的原始X和Y坐标，并转换为整数
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        // 如果当前的动作不是移动动作，记录最后的拦截坐标并返回false
        if (event.action != MotionEvent.ACTION_MOVE) {
            mLastInterceptX = x
            mLastInterceptY = y
            return false
        }

        // 计算X和Y方向上的位移差
        val deltaX = x - mLastInterceptX
        val deltaY = y - mLastInterceptY

        // 如果X方向的位移大于Y方向的位移，或者Y方向的位移小于或等于触摸阈值，则不消费触摸事件
        if (abs(deltaX.toDouble()) > abs(deltaY.toDouble()) || abs(deltaY.toDouble()) <= mTouchSlop) {
            return false
        }

        // 判断子容器是否应该滚动
        return shouldChildScroll(deltaY)
    }

    /**
     * 子容器是否需要消费滚动事件
     */
    private fun shouldChildScroll(deltaY: Int): Boolean {
        val childRecyclerView = findNestedScrollingChildRecyclerView() ?: return false
        return if (isScrollToBottom()) {
            // 父容器已经滚动到底部 且 向下滑动 且 子容器还没滚动到底部
            deltaY < 0 && !childRecyclerView.isScrollToBottom()
        } else {
            // 父容器还没滚动到底部 且 向上滑动 且 子容器已经滚动到顶部
            deltaY > 0 && !childRecyclerView.isScrollToTop()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mVelocity = 0
                mLastTouchY = ev.rawY
                childConsumeTouch = false
                childConsumeDistance = 0

                val childRecyclerView = findNestedScrollingChildRecyclerView()
                if (isScrollToBottom() && (childRecyclerView != null && !childRecyclerView.isScrollToTop())) {
                    stopScroll()
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                childConsumeTouch = false
                childConsumeDistance = 0
            }

            else -> {}
        }

        try {
            return super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // 检查是否滚动到底部
        if (isScrollToBottom()) {
            // 如果父容器已经滚动到底部，且向上滑动，且子容器还没滚动到顶部，事件传递给子容器
            // 获取子RecyclerView
            val childRecyclerView = findNestedScrollingChildRecyclerView()
            if (childRecyclerView != null) {
                // 计算Y方向的滑动距离
                val deltaY = (mLastTouchY - e.rawY).toInt()
                // 检查是否向上滑动或子RecyclerView是否未滚动到顶部
                if (deltaY >= 0 || !childRecyclerView.isScrollToTop()) {
                    // 添加滑动事件到速度追踪器
                    mVelocityTracker.addMovement(e)
                    // 如果手指抬起，处理fling操作
                    if (e.action == MotionEvent.ACTION_UP) {
                        // 计算当前的滑动速度
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
                        val velocityY = mVelocityTracker.yVelocity
                        // 如果当前速度大于最小fling速度，执行fling操作
                        if (abs(velocityY.toDouble()) > mMinimumFlingVelocity) {
                            childRecyclerView.fling(0, -velocityY.toInt())
                        }
                        // 清除速度追踪器
                        mVelocityTracker.clear()
                    } else {
                        // 否则，传递滑动事件给子RecyclerView
                        childRecyclerView.scrollBy(0, deltaY)
                    }

                    // 累计子容器消耗的滑动距离
                    childConsumeDistance += deltaY
                    // 更新最后触摸的Y坐标
                    mLastTouchY = e.rawY
                    // 标记子容器已消耗触摸事件
                    childConsumeTouch = true
                    return true
                }
            }
        }

        // 更新最后触摸的Y坐标
        mLastTouchY = e.rawY

        // 如果子容器已消耗触摸事件，调整事件参数并传递给父类处理
        if (childConsumeTouch) {
            // 在同一个事件序列中，子容器消耗了部分滑动距离，需要扣除掉
            val adjustedEvent = MotionEvent.obtain(
                e.downTime,
                e.eventTime,
                e.action,
                e.x,
                e.y + childConsumeDistance,  // 更新Y坐标以反映消耗的距离
                e.metaState
            )

            // 调用父类的onTouchEvent方法处理调整后的事件
            val handled = super.onTouchEvent(adjustedEvent)
            // 回收调整后的事件对象
            adjustedEvent.recycle()
            return handled
        }

        // 如果动作是抬起或取消，清除速度追踪器
        if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) {
            mVelocityTracker.clear()
        }

        // 尝试调用父类的onTouchEvent方法处理事件
        try {
            return super.onTouchEvent(e)
        } catch (ex: java.lang.Exception) {
            // 打印异常信息并返回false
            ex.printStackTrace()
            return false
        }
    }
    /**
     * 重写 fling 方法以自定义滑动行为。
     * 
     * @param velX 水平方向的速度。
     * @param velY 垂直方向的速度。
     * @return 如果滑动成功则返回 true，否则返回 false。
     */
    override fun fling(velX: Int, velY: Int): Boolean {
        // 调用父类的 fling 方法尝试进行滑动
        val fling = super.fling(velX, velY)
        // 如果 fling 返回 false 或者垂直速度 velY 小于等于0，将 mVelocity 设置为0
        // 否则，将 mVelocity 设置为 velY
        mVelocity = if (!fling || velY <= 0) {
            0
        } else {
            velY
        }
        // 返回 fling 的结果
        return fling
    }

    private fun dispatchChildFling() {
        // 检查是否滚动到底部并且存在非零的滚动速度
        if (isScrollToBottom() && mVelocity != 0) {
            // 尝试获取当前的滚动速度
            var mVelocity: Float = NestedOverScroller.invokeCurrentVelocity(this)
            // 如果当前速度非常小（接近于零），则将速度减半，以平滑处理
            if (abs(mVelocity.toDouble()) <= 2.0E-5f) {
                mVelocity = this.mVelocity.toFloat() * 0.5f
            } else {
                // 否则，将速度减少到原来的46%，以减缓滚动速度
                mVelocity *= 0.46f
            }
            // 查找当前嵌套的子RecyclerView
            val childRecyclerView: ChildRecyclerView? = findNestedScrollingChildRecyclerView()
            // 如果找到子RecyclerView，则使用计算后的速度进行fling操作
            childRecyclerView?.fling(0, mVelocity.toInt())
        }
        // 重置父容器的滚动速度为0
        mVelocity = 0
    }

    fun findNestedScrollingChildRecyclerView(): ChildRecyclerView? {
        if (adapter is INestedParentAdapter) {
            return (adapter as INestedParentAdapter).getCurrentChildRecyclerView()
        }
        return null
    }

    /**
     * 判断是否已滚动到底部
     * 
     * @return 如果无法继续向下滚动（即已到底部），返回true；否则返回false。
     */
    fun isScrollToBottom(): Boolean {
        return !canScrollVertically(1)
    }

    /**
     * 判断是否已滚动到顶部
     * 
     * @return 如果无法继续向上滚动（即已到顶部），返回true；否则返回false。
     */
    fun isScrollToTop(): Boolean {
        return !canScrollVertically(-1)
    }
    /**
     * 重写 `scrollToPosition` 方法以确保子RecyclerView也能相应地滚动到顶部。
     * 
     * @param position 想要滚动到的位置。
     */
    override fun scrollToPosition(position: Int) {
        // 检查是否需要使子RecyclerView滚动到顶部
        checkChildNeedScrollToTop(position)
        // 调用父类的方法以完成滚动操作
        super.scrollToPosition(position)
    }

    /**
     * 重写 `smoothScrollToPosition` 方法以确保子RecyclerView也能平滑滚动到顶部。
     * 
     * @param position 想要平滑滚动到的位置。
     */
    override fun smoothScrollToPosition(position: Int) {
        // 检查是否需要使子RecyclerView平滑滚动到顶部
        checkChildNeedScrollToTop(position)
        // 调用父类的方法以完成平滑滚动操作
        super.smoothScrollToPosition(position)
    }

    /**
     * 检查是否需要将子RecyclerView滚动到顶部。
     * 当父RecyclerView的滚动位置为0时，子RecyclerView也应滚动到顶部。
     * 
     * @param position 父RecyclerView想要滚动到的位置。
     */
    private fun checkChildNeedScrollToTop(position: Int) {
        // 如果父RecyclerView的目标位置是0，即顶部
        if (position == 0) {
            // 父容器滚动到顶部，从交互上来说子容器也需要滚动到顶部
            // 获取当前嵌套的子RecyclerView
            val childRecyclerView = findNestedScrollingChildRecyclerView()
            // 如果存在子RecyclerView，使其滚动到顶部
            childRecyclerView?.scrollToPosition(0)
        }
    }
}