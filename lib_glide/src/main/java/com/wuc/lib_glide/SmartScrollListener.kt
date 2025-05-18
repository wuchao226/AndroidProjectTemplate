package com.wuc.lib_glide

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.math.abs

/**
 * @author: wuc
 * @date: 2025/5/18
 * @description: 智能滚动监听器, 根据滚动速度动态调整图片加载优先级和质量
 * 1. 滚动速度检测：监听RecyclerView的滚动速度，根据滚动速度判断是否需要加载更多图片。
 */
class SmartScrollListener(private val context: Context) : RecyclerView.OnScrollListener() {
    private var scrollSpeed = 0
    private var lastScrollTime = 0L
    private var lastDy = 0

    /**
     * 滚动状态改变时回调
     */
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                // 停止滚动时，恢复正常加载
                scrollSpeed = 0
                resumeLoading()
            }

            RecyclerView.SCROLL_STATE_DRAGGING -> {
                // 开始拖动时，重置计时
                lastScrollTime = System.currentTimeMillis()
            }

            RecyclerView.SCROLL_STATE_SETTLING -> {
                // 惯性滚动时，继续监控速度
            }
        }
    }

    /**
     * 滚动时回调
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        // 计算滚动速度
        val currentTime = System.currentTimeMillis()
        val timeElapsed = currentTime - lastScrollTime

        if (timeElapsed > 0) {
            // 计算像素/毫秒的速度
            scrollSpeed = abs(dy - lastDy) / timeElapsed.toInt()
            lastScrollTime = currentTime
            lastDy = dy

            // 根据速度调整加载策略
            adjustLoadingStrategy()
        }
    }
    /**
     * 根据滚动速度调整加载策略
     */
    private fun adjustLoadingStrategy() {
        when {
            scrollSpeed > 3 -> {
                // 高速滚动时，暂停加载或加载缩略图
                pauseLoading()
            }
            scrollSpeed > 1 -> {
                // 中速滚动时，降低优先级
                lowPriorityLoading()
            }
            else -> {
                // 低速滚动时，正常加载
                resumeLoading()
            }
        }
    }
    /**
     * 暂停加载
     */
    private fun pauseLoading() {
        Glide.with(context).pauseRequests()
    }

    /**
     * 低优先级加载
     */
    private fun lowPriorityLoading() {
        Glide.with(context).resumeRequests()
        // 全局设置低优先级，实际使用时可以在具体的加载请求中设置
    }

    /**
     * 恢复正常加载
     */
    private fun resumeLoading() {
        Glide.with(context).resumeRequests()
    }

    /**
     * 获取当前滚动速度
     */
    fun getScrollSpeed(): Int {
        return scrollSpeed
    }
}