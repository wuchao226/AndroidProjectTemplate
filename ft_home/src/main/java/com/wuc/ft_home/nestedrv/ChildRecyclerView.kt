package com.wuc.ft_home.nestedrv

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.wuc.ft_home.R


/**
 * @author: wuc
 * @date: 2024/12/14
 * @description: 内层的RecyclerView
 */
class ChildRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr) {

    // 父级RecyclerView的引用，用于在嵌套滑动场景中与父级RecyclerView进行交互
    private var parentRecyclerView: ParentRecyclerView? = null

    // 触摸滑动的最小距离阈值，用于区分是否为滑动操作
    private val mTouchSlop: Int

    // 记录触摸事件按下时的X坐标
    private var downX: Float = 0f
    // 记录触摸事件按下时的Y坐标
    private var downY: Float = 0f

    // 当前的拖拽状态，初始化为静止状态
    private var dragState: Int = DRAG_IDLE

    companion object {
        // 拖拽状态：静止
        private const val DRAG_IDLE = 0
        // 拖拽状态：垂直拖拽
        private const val DRAG_VERTICAL = 1
        // 拖拽状态：水平拖拽
        private const val DRAG_HORIZONTAL = 2
    }

    init {
        // 获取当前上下文的视图配置信息
        val configuration = ViewConfiguration.get(context)
        // 从配置中获取触摸事件的最小滑动距离阈值，并赋值给mTouchSlop
        mTouchSlop = configuration.scaledTouchSlop
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        // 检查滚动状态是否已经停止
        if (state == SCROLL_STATE_IDLE) {
            // 获取当前RecyclerView的垂直滚动速度
            val velocityY = getVelocityY()
            // 如果速度小于0且垂直滚动偏移量为0，表示滚动到了顶部
            if (velocityY < 0 && computeVerticalScrollOffset() == 0) {
                // 在滚动到顶部时，如果ChildRecyclerView被分离(detach)，
                // 则将滚动事件传递给父级RecyclerView处理
                parentRecyclerView?.fling(0, velocityY)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // 检查触摸事件的类型
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // 如果是ACTION_DOWN事件，即用户开始触摸屏幕
            // 设置拖拽状态为静止
            dragState = DRAG_IDLE
            // 记录按下时的X坐标
            downX = ev.rawX
            // 记录按下时的Y坐标
            downY = ev.rawY
            // 停止任何正在进行的滑动动画
            this.stopFling()
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            // 如果是ACTION_MOVE事件，即用户在屏幕上移动手指
            // 计算X方向和Y方向上的移动距离
            val xDistance = Math.abs(ev.rawX - downX)
            val yDistance = Math.abs(ev.rawY - downY)
            // 判断移动方向
            if (xDistance > yDistance && xDistance > mTouchSlop) {
                // 如果X方向的移动距离大于Y方向的移动距离，并且大于系统定义的最小滑动距离
                // 判定为水平滑动，拦截事件
                return true
            } else if (xDistance == 0f && yDistance == 0f) {
                // 如果移动距离为0，判定为点击事件
                // 不拦截，交由子视图处理
                return super.onInterceptTouchEvent(ev)
            } else if (yDistance >= xDistance && yDistance > 8f) {
                // 如果Y方向的移动距离大于或等于X方向的移动距离，并且Y方向的移动距离大于8像素
                // 判定为垂直滑动，拦截事件
                return true
            }
        }
        // 其他情况，不拦截事件，交由子视图处理
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 这段逻辑主要是RecyclerView最底部，垂直上拉后居然还能左右滑动，不能忍
     */
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 处理触摸事件
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // 当用户开始触摸屏幕时，禁止父视图（ParentRecyclerView）拦截触摸事件
            // 这样可以确保ChildRecyclerView可以自由地处理滑动事件
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            // 当用户在屏幕上移动时，需要判断是水平滑动还是垂直滑动
            // 计算从按下到当前位置的X轴和Y轴的距离差
            val xDistance = Math.abs(ev.rawX - downX)
            val yDistance = Math.abs(ev.rawY - downY)

            // 判断滑动方向
            if (xDistance > yDistance && xDistance > mTouchSlop) {
                // 如果X轴的移动距离大于Y轴的移动距离，并且大于系统的最小滑动距离（mTouchSlop）
                // 则认为是水平滑动
                dragState = DRAG_HORIZONTAL
                // 在水平滑动时，允许父视图（如ViewPager或ViewPager2）拦截触摸事件
                // 这样可以处理ViewPager的左右滑动
                parent.requestDisallowInterceptTouchEvent(false)
            } else if (yDistance > xDistance && yDistance > mTouchSlop) {
                // 如果Y轴的移动距离大于X轴的移动距离，并且大于系统的最小滑动距离
                // 则认为是垂直滑动
                dragState = DRAG_VERTICAL
                // 在垂直滑动时，不允许父视图拦截触摸事件，确保ChildRecyclerView可以处理滑动事件
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        // 调用父类的onTouchEvent方法，以默认方式处理其他触摸事件
        return super.onTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        connectToParent()
    }

    /**
     * 跟ParentView建立连接，主要两件事情 -
     * 1. 将自己上报ViewPager/ViewPager2，通过tag关联到currentItem的View中
     * 2. 将ViewPager/ViewPager2报告给ParentRecyclerView
     * 这一坨代码需要跟ParentRecyclerView连起来看，否则可能会懵
     */
    private fun connectToParent() {
        var viewPager: ViewPager? = null
        var viewPager2: ViewPager2? = null
        var lastTraverseView: View = this

        // 从当前视图开始向上遍历父视图
        var parentView: View? = this.parent as View
        while (parentView != null) {
            // 获取当前父视图的类名
            val parentClassName = parentView::class.java.canonicalName
            // 检查是否是ViewPager2的内部实现类
            if ("androidx.viewpager2.widget.ViewPager2.RecyclerViewImpl" == parentClassName) {
                // 使用ViewPager2，parentView的顺序如下:
                // ChildRecyclerView -> 若干View -> FrameLayout -> RecyclerViewImpl -> ViewPager2 -> 若干View -> ParentRecyclerView

                // 此时lastTraverseView是上方注释中的FrameLayout，算是"ViewPager2.child"，我们此处将ChildRecyclerView设置到FrameLayout的tag中
                // 这个tag会在ParentRecyclerView中用到

                // 如果是ViewPager2的内部结构，设置tag以便在ParentRecyclerView中识别和使用
                // 此时lastTraverseView通常是FrameLayout，它是ViewPager2的直接子视图
                lastTraverseView.setTag(R.id.tag_saved_child_recycler_view, this)
            } else if (parentView is ViewPager) {
                // 使用ViewPager，parentView顺序如下：
                // ChildRecyclerView -> 若干View -> ViewPager -> 若干View -> ParentRecyclerView
                // 此处将ChildRecyclerView保存到ViewPager最直接的子View中
                // 如果当前父视图是ViewPager，记录下来，稍后会用于与ParentRecyclerView的交互
                viewPager = parentView
                // 将ChildRecyclerView标记在最接近的子视图上，以便ParentRecyclerView可以访问
                if (lastTraverseView != this) {
                    lastTraverseView.setTag(R.id.tag_saved_child_recycler_view, this)
                }
            } else if (parentView is ViewPager2) {
                // 如果当前父视图是ViewPager2，记录下来，稍后会用于与ParentRecyclerView的交互
                viewPager2 = parentView
            } else if (parentView is ParentRecyclerView) {
                // 如果遇到ParentRecyclerView，说明已经到达需要的层级，进行设置并结束遍历
                parentView.setInnerViewPager(viewPager)
                parentView.setInnerViewPager2(viewPager2)
                parentView.setChildPagerContainer(lastTraverseView)
                this.parentRecyclerView = parentView
                return
            }

            // 更新lastTraverseView和parentView以继续向上遍历
            lastTraverseView = parentView
            parentView = parentView.parent as View
        }
    }


    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        val x = e.rawX
        val y = e.rawY
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录触摸事件按下时的X和Y坐标
                downX = x
                downY = y
                // 请求父视图不要拦截触摸事件，以便子视图可以自由处理滑动
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                // 计算从按下到移动时的X轴和Y轴的距离差
                val dx: Float = x - downX
                val dy: Float = y - downY
                // 根据距离差判断滑动方向
                val orientation = getOrientation(dx, dy)
                // 获取当前视图在屏幕上的位置
                val location = intArrayOf(0, 0)
                getLocationOnScreen(location)
                when (orientation) {
                    "d" -> {
                        // 向下滑动处理
                        if (canScrollVertically(-1)) {
                            // 如果可以继续向下滑动，请求父视图不拦截事件
                            parent.requestDisallowInterceptTouchEvent(true)
                        } else {
                            // 如果已经滑动到顶部
                            if (dy < 24f) {
                                // 如果滑动距离小于24像素，继续请求父视图不拦截事件
                                parent.requestDisallowInterceptTouchEvent(true)
                            } else {
                                // 如果滑动距离大于或等于24像素，允许父视图拦截，以便父视图可以处理滑动
                                parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                    }
                    "u" -> {
                        // 向上滑动时，始终请求父视图不拦截事件，由子视图处理
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    "r" -> {
                        // 向右滑动时，暂无特殊处理，可以根据需要添加逻辑
                    }
                    "l" -> {
                        // 向左滑动时，暂无特殊处理，可以根据需要添加逻辑
                    }
                }
            }
        }
        // 调用父类的方法处理其他默认操作
        return super.dispatchTouchEvent(e)
    }

    /**
     * 根据X轴和Y轴的移动距离差(dx, dy)来判断手势的滑动方向。
     * 
     * @param dx X轴上的移动距离，正值表示向右滑动，负值表示向左滑动。
     * @param dy Y轴上的移动距离，正值表示向下滑动，负值表示向上滑动。
     * @return 返回一个字符串表示滑动的方向：
     *         "r" - 向右滑动
     *         "l" - 向左滑动
     *         "d" - 向下滑动
     *         "u" - 向上滑动
     */
    private fun getOrientation(dx: Float, dy: Float): String {
        // 判断X轴的移动是否大于Y轴的移动
        return if (Math.abs(dx) > Math.abs(dy)) {
            // 如果是，根据dx的正负判断是向右还是向左滑动
            if (dx > 0) "r" else "l"
        } else {
            // 否则，根据dy的正负判断是向下还是向上滑动
            if (dy > 0) "d" else "u"
        }
    }
}