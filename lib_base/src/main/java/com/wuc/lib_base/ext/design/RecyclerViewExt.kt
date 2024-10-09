package com.wuc.lib_base.ext.design

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewConfiguration
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_END
import androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_START
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wuc.lib_base.decoration.RVDividerItemDecoration
import com.wuc.lib_base.ext.dp

/**
  * @author: wuc
  * @date: 2024/10/9
  * @desc:  RecyclerView 扩展类
  */
/**
 * 设置空布局, 在 RecyclerView 数据为空的时候自动显示一个空布局
 * RecyclerView.setEmptyView(owner, emptyView)
 */
fun RecyclerView.setEmptyView(owner: LifecycleOwner, emptyView: View) =
    observeDataEmpty(owner) { emptyView.isVisible = it }

/**
 * 观察数据是否为空
 * RecyclerView.Adapter<*>.observeDataEmpty(owner) {...}
 */
fun RecyclerView.observeDataEmpty(owner: LifecycleOwner, block: (Boolean) -> Unit) =
    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        private var observer: RecyclerView.AdapterDataObserver? = null

        override fun onCreate(owner: LifecycleOwner) {
            if (observer == null) {
                val adapter = checkNotNull(adapter) {
                    "RecyclerView needs to set up the adapter before setting up an empty view."
                }
                observer = AdapterDataEmptyObserver(adapter, block)
                adapter.registerAdapterDataObserver(observer!!)
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            observer?.let {
                adapter?.unregisterAdapterDataObserver(it)
                observer = null
            }
        }
    })

/**
 * 顺滑地滚动到起始位置
 */
fun RecyclerView.smoothScrollToStartPosition(position: Int) =
    smoothScrollToPosition(position, SNAP_TO_START)

/**
 * 顺滑地滚动到末端位置
 */
fun RecyclerView.smoothScrollToEndPosition(position: Int) =
    smoothScrollToPosition(position, SNAP_TO_END)

/**
 * 顺滑地滚动到指定位置
 */
fun RecyclerView.smoothScrollToPosition(position: Int, snapPreference: Int) =
    layoutManager?.let {
        val smoothScroller = LinearSmoothScroller(context, snapPreference)
        smoothScroller.targetPosition = position
        it.startSmoothScroll(smoothScroller)
    }

/**
 * 创建一个 LinearSmoothScroller 对象
 */
fun LinearSmoothScroller(context: Context, snapPreference: Int) =
    object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference() = snapPreference
        override fun getHorizontalSnapPreference() = snapPreference
    }

/**
 * 列表每一项都加边距
 */
fun RecyclerView.addItemPadding(padding: Int) = addItemPadding(padding, padding, padding, padding)

/**
 * 列表每一项都加边距
 */
fun RecyclerView.addItemPadding(top: Int, bottom: Int, left: Int = 0, right: Int = 0) =
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.bottom = bottom
            outRect.top = top
            outRect.left = left
            outRect.right = right
        }
    })

/**
 * 适配器数据为空的观察者
 */
class AdapterDataEmptyObserver(
    private val adapter: RecyclerView.Adapter<*>,
    private val checkEmpty: (Boolean) -> Unit
) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() = checkEmpty(isDataEmpty)

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = checkEmpty(isDataEmpty)

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = checkEmpty(isDataEmpty)

    private val isDataEmpty get() = adapter.itemCount == 0
}

private var isLock = true

/**
 * 添加垂直滚动监听器
 */
fun RecyclerView.addOnVerticalScrollListener(
    onScrolledUp: (recyclerView: RecyclerView) -> Unit,
    onScrolledDown: (recyclerView: RecyclerView) -> Unit,
    onScrolledToTop: (recyclerView: RecyclerView) -> Unit,
    onScrolledToBottom: (recyclerView: RecyclerView) -> Unit,
    once: Boolean = true
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!recyclerView.canScrollVertically(-1)) {
                //顶部
                onScrolledUp.invoke(recyclerView)
            } else if (!recyclerView.canScrollVertically(1)) {
                //底部
                onScrolledDown.invoke(recyclerView)
            } else if (dy < 0 && Math.abs(dy) > ViewConfiguration.get(context).scaledTouchSlop) {
                //往上滑
                if (once && !isLock) {
                    isLock = true
                    onScrolledToTop.invoke(recyclerView)
                } else if (!once) {
                    onScrolledToTop.invoke(recyclerView)
                }
            } else if (dy > 0 && Math.abs(dy) > ViewConfiguration.get(context).scaledTouchSlop) {
                //往下滑
                if (once && isLock) {
                    isLock = false
                    onScrolledToBottom.invoke(recyclerView)
                } else if (!once) {
                    onScrolledToBottom.invoke(recyclerView)
                }
            }
        }
    })
}

/**
 * 设置分割线
 * @param color 分割线的颜色，默认是#DEDEDE
 * @param size 分割线的大小，默认是1px
 * @param includeLast 最后一条是否绘制
 */
fun RecyclerView.divider(
    color: Int = Color.parseColor("#DEDEDE"),
    size: Int = 1,
    includeLast: Boolean = true
): RecyclerView {
    val decoration = RVDividerItemDecoration(context, orientation)
    decoration.setDrawable(GradientDrawable().apply {
        setColor(color)
        shape = GradientDrawable.RECTANGLE
        setSize(size, size)
    })
    decoration.isDrawLastPositionDivider(includeLast)
    if (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
    addItemDecoration(decoration)
    return this
}

/**
 * 设置网格分割线间隔
 * @param spanCount 网格的列数
 * @param spacingDp 分割线的大小
 * @param includeEdge 是否包含边缘
 */
fun RecyclerView.dividerGridSpace(
    spanCount: Int,
    spacingDp: Float,
    includeEdge: Boolean
): RecyclerView {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column
            val targetSpacing = spacingDp.dp.toInt()
            if (includeEdge) {
                outRect.left =
                    targetSpacing - column * targetSpacing / spanCount // targetSpacing - column * ((1f / spanCount) * targetSpacing)
                outRect.right =
                    (column + 1) * targetSpacing / spanCount // (column + 1) * ((1f / spanCount) * targetSpacing)

                if (position < spanCount) { // top edge
                    outRect.top = targetSpacing
                }
                outRect.bottom = targetSpacing // item bottom
            } else {
                outRect.left =
                    column * targetSpacing / spanCount // column * ((1f / spanCount) * targetSpacing)
                outRect.right =
                    targetSpacing - (column + 1) * targetSpacing / spanCount // targetSpacing - (column + 1) * ((1f /    spanCount) * targetSpacing)
                if (position >= spanCount) {
                    outRect.top = targetSpacing // item top
                }
            }
        }
    })
    return this
}

/**
 * 设置 RecyclerView 为垂直布局
 * @param spanCount 网格的列数
 * @param isStaggered 是否为交错网格布局
 */
fun RecyclerView.vertical(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    if (spanCount != 0) {
        layoutManager = GridLayoutManager(context, spanCount)
    }
    if (isStaggered) {
        layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
    return this
}

/**
 * 设置 RecyclerView 的 ItemAnimator
 * @param animator ItemAnimator 对象
 */
fun RecyclerView.itemAnimator(animator: RecyclerView.ItemAnimator): RecyclerView {
    itemAnimator = animator
    return this
}

/**
 * 取消 RecyclerView 的 ItemAnimator
 */
fun RecyclerView.noItemAnim(){
    itemAnimator = null
}

/**
 * 设置 RecyclerView 为水平布局
 * @param spanCount 网格的列数
 * @param isStaggered 是否为交错网格布局
 */
fun RecyclerView.horizontal(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    if (spanCount != 0) {
        layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false)
    }
    if (isStaggered) {
        layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL)
    }
    return this
}

/**
 * 获取 RecyclerView 的布局方向
 */
inline val RecyclerView.orientation
    get() = if (layoutManager == null) -1 else layoutManager.run {
        when (this) {
            is LinearLayoutManager -> orientation
            is GridLayoutManager -> orientation
            is StaggeredGridLayoutManager -> orientation
            else -> -1
        }
    }