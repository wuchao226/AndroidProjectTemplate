package com.wuc.ft_home.nestedrv

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field


/**
 * @author: wuc
 * @date: 2024/12/14
 * @description: 平滑滚动效果的辅助类
 */
object NestedOverScroller {
    fun invokeCurrentVelocity(@NonNull rv: RecyclerView): Float {
        try {
            var viewFlinger: Field? = null
            var superClass: Class<*>? = rv.javaClass.superclass
            // 循环遍历父类，寻找名为"mViewFlinger"的字段
            while (superClass != null) {
                try {
                    viewFlinger = superClass.getDeclaredField("mViewFlinger")
                    break
                } catch (ignored: Throwable) {
                    // 如果当前类中没有找到，继续在上一级父类中查找
                }
                superClass = superClass.superclass
            }

            // 如果没有找到viewFlinger字段，返回0.0f
            if (viewFlinger == null) {
                return 0.0f
            } else {
                // 设置私有字段可访问
                viewFlinger.isAccessible = true
                val viewFlingerValue: Any? = viewFlinger.get(rv)
                // 获取viewFlinger中名为"mScroller"的字段
                val scroller: Field? = viewFlingerValue?.javaClass?.getDeclaredField("mScroller")
                scroller?.isAccessible = true
                val scrollerValue: Any? = scroller?.get(viewFlingerValue)
                // 获取scroller中名为"mScrollerY"的字段
                val scrollerY: Field? = scrollerValue?.javaClass?.getDeclaredField("mScrollerY")
                scrollerY?.isAccessible = true
                val scrollerYValue: Any? = scrollerY?.get(scrollerValue)
                // 获取scrollerY中名为"mCurrVelocity"的字段，用于获取当前滚动速度
                val currVelocity: Field? = scrollerYValue?.javaClass?.getDeclaredField("mCurrVelocity")
                currVelocity?.isAccessible = true
                return (currVelocity?.get(scrollerYValue) ?: 0.0f) as Float
            }
        } catch (ignored: Throwable) {
            // 如果过程中发生任何异常，返回0.0f
            return 0.0f
        }
    }
}