package com.wuc.ft_home.nestedrv

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.OverScroller
import androidx.recyclerview.widget.RecyclerView
import com.wuc.lib_base.log.WLogUtils
import java.lang.reflect.Field

/**
 * @author: wuc
 * @date: 2024/12/16
 * @description:
 */
@SuppressLint("DiscouragedPrivateApi")
open class BaseRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private lateinit var overScroller: OverScroller

    private lateinit var scrollerYObj: Any

    private lateinit var velocityYField: Field

    init {
        runCatching {
            // 1. mViewFlinger对象获取
            val viewFlingField = RecyclerView::class.java.getDeclaredField("mViewFlinger")
            viewFlingField.isAccessible = true
            val viewFlingObj = viewFlingField.get(this)

            // 2. overScroller对象获取
            val overScrollerFiled = viewFlingObj.javaClass.getDeclaredField("mOverScroller")
            overScrollerFiled.isAccessible = true
            overScroller = overScrollerFiled.get(viewFlingObj) as OverScroller

            // 3. scrollerY对象获取
            val scrollerYFiled: Field = OverScroller::class.java.getDeclaredField("mScrollerY")
            scrollerYFiled.isAccessible = true
            scrollerYObj = scrollerYFiled.get(overScroller)

            // 4. Y轴速率filed获取
            velocityYField = scrollerYObj.javaClass.getDeclaredField("mCurrVelocity")
            velocityYField.isAccessible = true
        }.onFailure {
            WLogUtils.e("BaseRecyclerView init error ${it.message}")
        }
    }

    /**
     * 获取垂直方向的速率
     */
    fun getVelocityY(): Int {
        return runCatching {
            (velocityYField.get(scrollerYObj) as Float).toInt()
        }.getOrElse {
            WLogUtils.e("获取垂直方向速率失败: ${it.message}")
            0
        }
    }

    /**
     * 停止滑动fling
     */
    fun stopFling() {
        runCatching {
            this.overScroller.forceFinished(true)
            velocityYField.set(scrollerYObj, 0)
        }.onFailure {
            WLogUtils.e("BaseRecyclerView stopFling error ${it.message}")
        }
    }
}