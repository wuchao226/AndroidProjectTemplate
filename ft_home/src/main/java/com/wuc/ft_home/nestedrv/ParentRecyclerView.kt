package com.wuc.ft_home.nestedrv

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.wuc.ft_home.R


/**
 * @author: wuc
 * @date: 2024/12/14
 * @description: 外层的RecyclerView
 */
class ParentRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr), NestedScrollingParent3 {
    // 存储子页面容器的引用，用于管理内部ViewPager或ViewPager2
    private var childPagerContainer: View? = null

    // 内部使用的传统ViewPager的引用
    private var innerViewPager: ViewPager? = null

    // 内部使用的新版ViewPager2的引用
    private var innerViewPager2: ViewPager2? = null

    // 标记是否不拦截触摸事件，true表示不拦截
    private var doNotInterceptTouchEvent: Boolean = false

    // 用于通知外部监听器顶部粘性状态变化的回调函数
    private var stickyListener: ((isAtTop: Boolean) -> Unit)? = null

    // 标记内部内容是否处于顶部粘性位置
    private var innerIsStickyTop = false

    /**
     * 顶部悬停的高度
     */
    private var stickyHeight = 0

    init {
        // 设置滚动模式为不显示滚动条，即在滚动到边界时不显示那种“弹性”效果
        this.overScrollMode = View.OVER_SCROLL_NEVER
        // 设置视图组的焦点行为，阻止子视图在被添加时自动获取焦点
        this.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        // 检查事件是否为按下动作
        if (e!!.action == MotionEvent.ACTION_DOWN) {
            // 查找当前活动的子RecyclerView
            val childRecyclerView = findCurrentChildRecyclerView()

            // 判断是否应该禁止拦截触摸事件
            // 如果返回true，则不拦截，允许子视图处理触摸事件
            doNotInterceptTouchEvent = doNotInterceptTouch(e.rawY, childRecyclerView)

            // 停止当前RecyclerView及其子RecyclerView的滚动动作
            this.stopFling()
            childRecyclerView?.stopFling()
        }

        // 根据doNotInterceptTouchEvent的值决定是否拦截触摸事件
        // 如果为true，则不拦截（返回false），事件可以传递给子视图
        // 如果为false，则调用父类的onInterceptTouchEvent方法来决定是否拦截
        return if (doNotInterceptTouchEvent) {
            false
        } else {
            super.onInterceptTouchEvent(e)
        }
    }


    /**
     * 判断是否不拦截触摸事件。
     * 如果子RecyclerView或者子页面容器不存在，或者子页面容器没有附加到窗口上，直接返回false，表示不禁止拦截。
     * 如果存在并且已经附加到窗口上，进行进一步的位置判断。
     *
     * @param rawY 触摸事件的Y坐标
     * @param childRecyclerView 当前活动的子RecyclerView
     * @return true 如果不应该拦截触摸事件，否则返回false
     */
    private fun doNotInterceptTouch(rawY: Float, childRecyclerView: ChildRecyclerView?): Boolean {
        // 检查子RecyclerView和childPagerContainer是否非空且已经附加到窗口
        if (null != childRecyclerView && null != childPagerContainer &&
            ViewCompat.isAttachedToWindow(childPagerContainer!!)
        ) {
            // 获取子RecyclerView在屏幕上的位置
            val coorValue = IntArray(2)
            childRecyclerView.getLocationOnScreen(coorValue)

            // 获取子RecyclerView的Y坐标
            val childRecyclerViewY = coorValue[1]
            // 如果触摸位置在子RecyclerView的Y坐标之下，返回true，不拦截触摸事件
            if (rawY > childRecyclerViewY) {
                return true
            }

            // 如果子页面容器的顶部位置等于顶部悬停的高度，也返回true，不拦截触摸事件
            if (childPagerContainer!!.top == stickyHeight) {
                return true
            }
        }

        // 如果以上条件都不满足，默认返回false，表示拦截触摸事件
        return false
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        // 检查目标视图是否为ChildRecyclerView类型
        if (target is ChildRecyclerView) {
            // 初始化consumeY为滑动距离dy
            var consumeY = dy
            // 获取子RecyclerView的垂直滚动偏移量
            val childScrollY = target.computeVerticalScrollOffset()
            // 判断子页面容器的顶部是否高于stickyHeight
            if (childPagerContainer!!.top > stickyHeight) {
                // 如果子视图已滚动且向下滚动(dy<0)，不消耗滚动距离
                if (childScrollY > 0 && dy < 0) {
                    consumeY = 0
                } else if (childPagerContainer!!.top - dy < stickyHeight) {
                    // 如果滚动后子页面容器顶部低于stickyHeight，调整consumeY以使其不低于stickyHeight
                    consumeY = childPagerContainer!!.top - stickyHeight
                }
            } else if (childPagerContainer!!.top == stickyHeight) {
                // 如果子页面容器顶部已经等于stickyHeight，根据子视图滚动偏移调整consumeY
                consumeY = if (-dy < childScrollY) {
                    0
                } else {
                    dy + childScrollY
                }
            }

            // 如果计算出的consumeY不为0，更新consumed数组并滚动父RecyclerView
            if (consumeY != 0) {
                consumed[1] = consumeY
                this.scrollBy(0, consumeY)
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        // 检查滚动状态是否为停止状态
        if (state == SCROLL_STATE_IDLE) {
            // 获取当前滚动的Y轴速度
            val velocityY = getVelocityY()
            // 如果Y轴速度大于0，表示有向下滚动的趋势
            if (velocityY > 0) {
                // 滑动到最底部时，骤然停止，这时需要把速率传递给ChildRecyclerView
                // 查找当前显示的ChildRecyclerView
                val childRecyclerView = findCurrentChildRecyclerView()
                // 如果找到了ChildRecyclerView，则调用fling方法传递速度，实现平滑滚动
                childRecyclerView?.fling(0, velocityY)
            }
        }
    }

    /**
     * 确定是否应该开始嵌套滚动。
     * 
     * @param child 直接与此视图相关联的子视图。
     * @param target 发起嵌套滚动操作的目标视图。
     * @param axes 滚动方向的轴（水平或垂直）。
     * @param type 触发滚动的类型（触摸或程序性）。
     * @return 如果目标视图是ChildRecyclerView，则返回true，表示此视图愿意配合目标视图一起参与嵌套滚动。
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return target is ChildRecyclerView
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        // do nothing
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        // do nothing
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        // do nothing
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        // do nothing
    }

    /**
     * 获取当前的ChildRecyclerView
     */
    /**
     * 查找当前活动的ChildRecyclerView实例。
     * 此方法首先检查是否存在innerViewPager，如果存在，则遍历其所有子视图，
     * 并通过反射获取每个子视图的位置，以确定当前活动的视图。
     * 如果找到ChildRecyclerView实例，则返回它；否则，尝试从视图的标签中获取。
     * 如果使用的是ViewPager2，过程类似，但是通过LayoutManager来寻找当前视图。
     * 
     * @return 当前活动的ChildRecyclerView，如果没有找到则返回null。
     */
    private fun findCurrentChildRecyclerView(): ChildRecyclerView? {
        // 检查是否有ViewPager实例
        if (innerViewPager != null) {
            val currentItem = innerViewPager!!.currentItem
            // 遍历ViewPager的所有子视图
            for (i in 0 until innerViewPager!!.childCount) {
                val itemChildView = innerViewPager!!.getChildAt(i)
                val layoutParams = itemChildView.layoutParams as ViewPager.LayoutParams
                // 使用反射获取视图的位置
                val positionField = layoutParams.javaClass.getDeclaredField("position")
                positionField.isAccessible = true
                val position = positionField.get(layoutParams) as Int

                // 检查当前视图是否为活动视图且不是装饰视图
                if (!layoutParams.isDecor && currentItem == position) {
                    // 如果是ChildRecyclerView，直接返回
                    if (itemChildView is ChildRecyclerView) {
                        return itemChildView
                    } else {
                        // 否则尝试从标签中获取ChildRecyclerView
                        val tagView = itemChildView?.getTag(R.id.tag_saved_child_recycler_view)
                        if (tagView is ChildRecyclerView) {
                            return tagView
                        }
                    }
                }
            }
        } else if (innerViewPager2 != null) {
            // 对于ViewPager2，使用LayoutManager来找到当前视图
            val layoutManagerFiled = ViewPager2::class.java.getDeclaredField("mLayoutManager")
            layoutManagerFiled.isAccessible = true
            val pagerLayoutManager = layoutManagerFiled.get(innerViewPager2) as LinearLayoutManager
            val currentChild = pagerLayoutManager.findViewByPosition(innerViewPager2!!.currentItem)

            // 检查当前视图是否为ChildRecyclerView
            if (currentChild is ChildRecyclerView) {
                return currentChild
            } else {
                // 否则尝试从标签中获取ChildRecyclerView
                val tagView = currentChild?.getTag(R.id.tag_saved_child_recycler_view)
                if (tagView is ChildRecyclerView) {
                    return tagView
                }
            }
        }
        // 如果没有找到，返回null
        return null
    }

    fun setInnerViewPager(viewPager: ViewPager?) {
        this.innerViewPager = viewPager
    }

    fun setInnerViewPager2(viewPager2: ViewPager2?) {
        this.innerViewPager2 = viewPager2
    }

   
    /**
     * 由ChildRecyclerView上报ViewPager(ViewPager2)的父容器，用做内联滑动逻辑判断，及Touch拦截等
     * 设置子页面容器的引用，并在引用更新后调整其高度。
     * @param childPagerContainer 新的子页面容器视图。
     */
    fun setChildPagerContainer(childPagerContainer: View) {
        // 检查传入的容器是否与当前容器不同
        if (this.childPagerContainer != childPagerContainer) {
            // 更新子页面容器的引用
            this.childPagerContainer = childPagerContainer
            // 在主线程中调整子页面容器的高度
            this.post {
                adjustChildPagerContainerHeight()
            }
        }
    }

   
    /**
     * Activity调用方法
     * 设置粘性高度并调整滚动位置。
     * 此方法用于设置粘性头部的高度，并根据新旧高度差调整滚动位置，以保持内容位置不变。
     * @param stickyHeight 新的粘性头部高度。
     */
    fun setStickyHeight(stickyHeight: Int) {
        // 计算新旧粘性高度的差值
        val scrollOffset = this.stickyHeight - stickyHeight
        // 更新粘性高度
        this.stickyHeight = stickyHeight
        // 调整子页面容器的高度
        this.adjustChildPagerContainerHeight()

        // 在主线程中，根据高度差进行滚动调整，以保持内容的相对位置不变
        this.post {
            this.scrollBy(0, scrollOffset)
        }
    }

    /**
     * 调整Child容器的高度
     */
    /**
     * 调整子页面容器的高度以适应粘性头部的变化。
     * 此方法检查子页面容器是否存在，如果存在，则计算并设置其正确的高度。
     * 新的高度是父容器的总高度减去粘性头部的高度。
     */
    private fun adjustChildPagerContainerHeight() {
        // 检查子页面容器是否已经设置
        if (null != childPagerContainer) {
            // 获取当前子页面容器的布局参数
            val layoutParams = childPagerContainer!!.layoutParams
            // 计算新的高度值
            val newHeight = this.height - stickyHeight
            // 如果新的高度与当前高度不同，则更新高度
            if (newHeight != layoutParams.height) {
                layoutParams.height = newHeight
                // 应用新的布局参数到子页面容器
                childPagerContainer!!.layoutParams = layoutParams
            }
        }
    }

    /**
     * 吸顶回调
     */
    fun setStickyListener(stickyListener: (isAtTop: Boolean) -> Unit) {
        this.stickyListener = stickyListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        // 调用父类的onScrollChanged方法
        super.onScrollChanged(l, t, oldl, oldt)

        // 初始化当前是否处于粘性顶部的标志为false
        var currentStickyTop = false

        // 检查childPagerContainer是否非空且其顶部位置是否与stickyHeight相等
        if (childPagerContainer != null && childPagerContainer?.top == stickyHeight) {
            // 如果条件满足，设置当前处于粘性顶部的标志为true
            currentStickyTop = true
        }

        // 检查当前的粘性顶部状态是否与之前的状态不同
        if (currentStickyTop != innerIsStickyTop) {
            // 更新内部的粘性顶部状态
            innerIsStickyTop = currentStickyTop
            // 如果有设置粘性状态变化的监听器，则调用该监听器
            stickyListener?.invoke(innerIsStickyTop)
        }
    }

    /**
     * 处理嵌套滑动的预抛动作。
     * 
     * @param target 触发滑动事件的视图。
     * @param velocityX 水平方向的速度。
     * @param velocityY 垂直方向的速度。
     * @return 如果消耗了抛动事件则返回true，否则返回false。
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        try {
            // 检查Android系统版本是否低于或等于19
            if (android.os.Build.VERSION.SDK_INT <= 19) {
                // 对于Android系统版本19及以下，直接消耗掉这个事件
                return true
            }
            // 调用父类的onNestedPreFling方法处理抛动事件
            return super.onNestedPreFling(target, velocityX, velocityY)
        } catch (e: Exception) {
            // 如果发生异常，也消耗掉这个事件
            return true
        }
    }
}