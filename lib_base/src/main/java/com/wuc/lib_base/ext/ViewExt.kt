package com.wuc.lib_base.ext

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView
import com.wuc.lib_base.R
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * View相关的扩展方法
 * 包括防止重复点击、增加点击区域、设置圆角、判断触摸位置、获取自定义属性等。
 * 作者：wuc
 * 日期：2024/10/9
 */

// 定义一个私有的扩展属性，用于记录 View 的最后点击时间
private var View.lastClickTime: Long? by viewTags(R.id.tag_last_click_time)

// 定义一个全局变量，表示防抖点击的时间间隔，默认为 500 毫秒
var debouncingClickIntervals = 500

// 为 List<View> 扩展一个函数，设置防抖点击事件
fun List<View>.doOnDebouncingClick(
    clickIntervals: Int = debouncingClickIntervals,
    isSharingIntervals: Boolean = false,
    block: () -> Unit
) =
    forEach { it.doOnDebouncingClick(clickIntervals, isSharingIntervals, block) }


/**
 * 给 [View] 设置带有防抖效果的点击事件
 *
 * @receiver [View]
 * @param clickIntervals Int 防抖间隔时间，单位是毫秒，默认值为全局变量 debouncingClickIntervals
 * @param isSharingIntervals Boolean 是否共享点击间隔时间，默认为 false
 * @param block () -> Unit 具体的点击事件
 */
fun View.doOnDebouncingClick(
    clickIntervals: Int = debouncingClickIntervals,
    isSharingIntervals: Boolean = false,
    block: () -> Unit
) {
    // 设置点击监听器
    setOnClickListener {
        // 根据 isSharingIntervals 参数决定使用哪个 View 记录点击时间
        val view = if (isSharingIntervals) {
            // 如果共享点击间隔时间，则使用 Activity 的 decorView
            context.asActivity()?.window?.decorView ?: this
        } else {
            // 否则使用当前 View
            this
        }
        // 获取当前时间
        val currentTime = System.currentTimeMillis()
        // 判断当前时间与上次点击时间的间隔是否大于 clickIntervals
        if (currentTime - (view.lastClickTime ?: 0L) > clickIntervals) {
            // 更新上次点击时间
            view.lastClickTime = currentTime
            // 执行点击事件
            block()
        }
    }
}

// 为 List<View> 扩展一个函数，设置普通点击事件
inline fun List<View>.doOnClick(crossinline block: () -> Unit) = forEach { it.doOnClick(block) }

// 为 View 扩展一个函数，设置普通点击事件
inline fun View.doOnClick(crossinline block: () -> Unit) = setOnClickListener { block() }

// 为 List<View> 扩展一个函数，设置防抖长按事件
fun List<View>.doOnDebouncingLongClick(
    clickIntervals: Int = debouncingClickIntervals,
    isSharingIntervals: Boolean = false,
    block: () -> Unit
) =
    forEach { it.doOnDebouncingLongClick(clickIntervals, isSharingIntervals, block) }

/**
 * 给 [View] 设置带有防抖效果的长按点击事件
 *
 * @receiver [View]
 * @param clickIntervals Int 防抖间隔时间，单位是毫秒，默认值为全局变量 debouncingClickIntervals
 * @param isSharingIntervals Boolean 是否共享点击间隔时间，默认为 false
 * @param block () -> Unit 具体的点击事件
 */
fun View.doOnDebouncingLongClick(
    clickIntervals: Int = debouncingClickIntervals,
    isSharingIntervals: Boolean = false,
    block: () -> Unit
) =
    doOnLongClick {
        val view = if (isSharingIntervals) context.asActivity()?.window?.decorView ?: this else this
        val currentTime = System.currentTimeMillis()
        if (currentTime - (view.lastClickTime ?: 0L) > clickIntervals) {
            view.lastClickTime = currentTime
            block()
        }
    }

// 为 List<View> 扩展一个函数，设置普通长按事件
inline fun List<View>.doOnLongClick(crossinline block: () -> Unit) = forEach { it.doOnLongClick(block) }

// 为 View 扩展一个函数，设置普通长按事件
inline fun View.doOnLongClick(crossinline block: () -> Unit) =
    setOnLongClickListener {
        block()
        true
    }

/**
 * 增大点击区域
 * View.expandClickArea(...)
 */
fun View.expandClickArea(expandSize: Float) = expandClickArea(expandSize.toInt())

fun View.expandClickArea(expandSize: Int) =
    expandClickArea(expandSize, expandSize, expandSize, expandSize)

fun View.expandClickArea(top: Float, left: Float, right: Float, bottom: Float) =
    expandClickArea(top.toInt(), left.toInt(), right.toInt(), bottom.toInt())

/**
 * 扩大 View 的点击区域
 *
 * @param top    向上扩大的距离
 * @param left   向左扩大的距离
 * @param right  向右扩大的距离
 * @param bottom 向下扩大的距离
 */
fun View.expandClickArea(top: Int, left: Int, right: Int, bottom: Int) {
    // 获取 View 的父容器，如果父容器不是 ViewGroup 类型，则直接返回
    val parent = parent as? ViewGroup ?: return

    // 在父容器的消息队列中添加一个任务，以确保在布局完成后执行
    parent.post {
        // 创建一个 Rect 对象，用于存储 View 的点击区域
        val rect = Rect()

        // 获取 View 的点击区域，并存储到 rect 中
        getHitRect(rect)

        // 调整 rect 的边界，扩大点击区域
        rect.top -= top
        rect.left -= left
        rect.right += right
        rect.bottom -= bottom

        // 获取父容器的 touchDelegate
        val touchDelegate = parent.touchDelegate

        // 如果 touchDelegate 为空或不是 MultiTouchDelegate 类型，则创建一个新的 MultiTouchDelegate
        if (touchDelegate == null || touchDelegate !is MultiTouchDelegate) {
            parent.touchDelegate = MultiTouchDelegate(rect, this)
        } else {
            // 如果 touchDelegate 是 MultiTouchDelegate 类型，则更新其点击区域
            touchDelegate.put(rect, this)
        }
    }
}

/**
 * 设置圆角
 * View.roundCorners
 */
var View.roundCorners: Float
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR)
    get() = noGetter()
    set(value) {
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, value)
            }
        }
    }

/**
 * 判断控件是否在触摸位置上
 */
fun View?.isTouchedAt(x: Float, y: Float): Boolean =
    isTouchedAt(x.toInt(), y.toInt())

fun View?.isTouchedAt(x: Int, y: Int): Boolean =
    this?.locationOnScreen?.contains(x, y) == true

/**
 * 寻找触摸位置上的子控件
 */
fun View.findTouchedChild(x: Float, y: Float): View? =
    findTouchedChild(x.toInt(), y.toInt())

fun View.findTouchedChild(x: Int, y: Int): View? =
    touchables.find { it.isTouchedAt(x, y) }

/**
 * 获取控件在屏幕的位置
 */
inline val View.locationOnScreen: Rect
    get() = IntArray(2).let {
        getLocationOnScreen(it)
        Rect(it[0], it[1], it[0] + width, it[1] + height)
    }

/**
 * 使用自定义属性
 */
inline fun View.withStyledAttrs(
    set: AttributeSet?,
    @StyleableRes attrs: IntArray,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    block: TypedArray.() -> Unit
) =
    context.withStyledAttributes(set, attrs, defStyleAttr, defStyleRes, block)

/**
 * 获取自定义属性
 */
inline fun View.withStyledAttributes(
    set: AttributeSet?,
    @StyleableRes attrs: IntArray,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    block: TypedArray.() -> Unit
) =
    context.withStyledAttributes(set, attrs, defStyleAttr, defStyleRes, block)

/**
 * 获取根视图的 WindowInsetsCompat
 */
val View.rootWindowInsetsCompat: WindowInsetsCompat? by viewTags(R.id.tag_root_window_insets) {
    ViewCompat.getRootWindowInsets(this)
}

/**
 * 获取 WindowInsetsControllerCompat
 */
val View.windowInsetsControllerCompat: WindowInsetsControllerCompat? by viewTags(R.id.tag_window_insets_controller) {
//    ViewCompat.getWindowInsetsController(this)
    (this.context as? Activity)?.window?.let { window ->
//        WindowInsetsControllerCompat(window, this)
        WindowCompat.getInsetsController(window, this)
    }
}

/**
 * 监听应用 WindowInsets
 */
fun View.doOnApplyWindowInsets(action: (View, WindowInsetsCompat) -> WindowInsetsCompat) =
    ViewCompat.setOnApplyWindowInsetsListener(this, action)

// 定义一个函数，用于获取和设置 View 的标签
fun <T> viewTags(key: Int) = object : ReadWriteProperty<View, T?> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: View, property: KProperty<*>) =
        thisRef.getTag(key) as? T

    override fun setValue(thisRef: View, property: KProperty<*>, value: T?) =
        thisRef.setTag(key, value)
}

// 定义一个函数，用于获取和设置 View 的标签，并在标签为空时执行一个块
@Suppress("UNCHECKED_CAST")
fun <T> viewTags(key: Int, block: View.() -> T) = ReadOnlyProperty<View, T> { thisRef, _ ->
    if (thisRef.getTag(key) == null) {
        thisRef.setTag(key, block(thisRef))
    }
    thisRef.getTag(key) as T
}

// 定义一个类，用于处理多重触摸代理
/**
 * MultiTouchDelegate 是一个处理多重触摸代理的类，它继承自 TouchDelegate。
 * 它允许在一个 View 上设置多个触摸代理，并根据触摸事件的位置将事件分发给相应的代理。
 */
class MultiTouchDelegate(bound: Rect, delegateView: View) : TouchDelegate(bound, delegateView) {
    // 存储 View 和其对应的 Rect 和 TouchDelegate 的映射关系
    private val map = mutableMapOf<View, Pair<Rect, TouchDelegate>>()
    // 当前目标的 TouchDelegate
    private var targetDelegate: TouchDelegate? = null

    init {
        // 初始化时将传入的 bound 和 delegateView 添加到 map 中
        put(bound, delegateView)
    }

    /**
     * 将新的 bound 和 delegateView 添加到 map 中
     * @param bound 触摸区域
     * @param delegateView 代理的 View
     */
    fun put(bound: Rect, delegateView: View) {
        map[delegateView] = bound to TouchDelegate(bound, delegateView)
    }

    /**
     * 处理触摸事件
     * @param event 触摸事件
     * @return 是否处理了触摸事件
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 获取触摸事件的 x 和 y 坐标
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.actionMasked) {
            // 当触摸事件是 ACTION_DOWN 时，找到包含触摸点的 TouchDelegate
            MotionEvent.ACTION_DOWN -> {
                targetDelegate = map.entries.find { it.value.first.contains(x, y) }?.value?.second
            }
            // 当触摸事件是 ACTION_CANCEL 时，清空当前目标的 TouchDelegate
            MotionEvent.ACTION_CANCEL -> {
                targetDelegate = null
            }
        }
        // 将触摸事件传递给当前目标的 TouchDelegate
        return targetDelegate?.onTouchEvent(event) ?: false
    }
}


/**
 * 获取View的截图, 支持获取整个RecyclerView列表的长截图
 * 注意：调用该方法时，请确保View已经测量完毕，如果宽高为0，则将抛出异常
 */
fun View.toBitmap(): Bitmap {
    if (measuredWidth == 0 || measuredHeight == 0) {
        throw RuntimeException("调用该方法时，请确保View已经测量完毕，如果宽高为0，则抛出异常以提醒！")
    }
    return when (this) {
        is RecyclerView -> {
            this.scrollToPosition(0)
            this.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val bmp = Bitmap.createBitmap(width, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)

            //draw default bg, otherwise will be black
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            this.draw(canvas)
            //恢复高度
            this.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
            )
            bmp //return
        }
        else -> {
            val screenshot =
                Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(screenshot)
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            draw(canvas)// 将 view 画到画布上
            screenshot //return
        }
    }
}

/*************************************** View可见性相关 ********************************************/
/**
 * 隐藏View
 * @receiver View
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 显示View
 * @receiver View
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * View不可见但存在原位置
 * @receiver View
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

val View.isGone: Boolean
    get() {
        return visibility == View.GONE
    }

val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

val View.isInvisible: Boolean
    get() {
        return visibility == View.INVISIBLE
    }

/**
 * 切换View的可见性
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}

/**
 * 设置 View 为 [View.VISIBLE]
 * 如果 [isVisible] 值为true，将 [View.setVisibility] 设置为 [View.VISIBLE],反之为 [View.GONE]
 *
 * @receiver View
 * @param isVisible Boolean 是否显示
 */
fun View.setVisible(isVisible: Boolean) {
    if (isVisible) visible() else gone()
}

/**
 * 设置 View 为 [View.GONE]
 * 如果 [isGone] 值为true，将 [View.setVisibility] 设置为 [View.GONE],反之为 [View.VISIBLE]
 *
 * @receiver View
 * @param isGone Boolean 是否隐藏
 */
fun View.setGone(isGone: Boolean) {
    if (isGone) visible() else gone()
}


/*************************************** View宽高相关 ********************************************/
/**
 * 设置 View 的高度
 * @receiver View
 * @param height Int 目标高度
 * @return View
 */
fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View的宽度
 * @receiver View
 * @param width Int 目标宽度
 * @return View
 */
fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

/**
 * 设置View的宽度和高度
 * @receiver View
 * @param width Int 要设置的宽度
 * @param height Int 要设置的高度
 * @return View
 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置宽度，带有过渡动画
 * @param targetValue 目标宽度
 * @param duration 时长
 * @param action 可选行为
 * @return 动画
 */
fun View.animateWidth(
    targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/**
 * 设置高度，带有过渡动画
 * @param targetValue 目标高度
 * @param duration 时长
 * @param action 可选行为
 * @return 动画
 */
fun View.animateHeight(
    targetValue: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/**
 * 设置宽度和高度，带有过渡动画
 * @param targetWidth 目标宽度
 * @param targetHeight 目标高度
 * @param duration 时长
 * @param action 可选行为
 * @return 动画
 */
fun View.animateWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        animator = ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, targetHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/*************************************** View其他 ********************************************/
/**
 * 获取View id
 */
fun View.getViewId(): Int {
    var id = id
    if (id == View.NO_ID) {
        id = View.generateViewId()
    }
    return id
}