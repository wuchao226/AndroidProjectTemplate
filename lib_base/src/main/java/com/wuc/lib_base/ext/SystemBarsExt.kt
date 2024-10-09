package com.wuc.lib_base.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.wuc.lib_base.R

/**
 * 这个文件定义了一些扩展函数和属性，用于增强 Fragment 和 Activity 的系统栏（状态栏和导航栏）功能。
 * 包括设置沉浸式状态栏、透明状态栏、状态栏颜色、状态栏可见性、导航栏颜色、导航栏可见性等。
 * 作者：wuc
 * 日期：2024/10/9
 */

// 定义私有扩展属性，用于记录 View 是否已经添加了顶部或底部的边距或内边距
private var View.isAddedMarginTop: Boolean? by viewTags(R.id.tag_is_added_margin_top)
private var View.isAddedPaddingTop: Boolean? by viewTags(R.id.tag_is_added_padding_top)
private var View.isAddedMarginBottom: Boolean? by viewTags(R.id.tag_is_added_margin_bottom)

// 为 Fragment 扩展一个函数，设置沉浸式状态栏
fun Fragment.immerseStatusBar(lightMode: Boolean = true) {
    activity?.immerseStatusBar(lightMode)
}

// 为 Activity 扩展一个函数，设置沉浸式状态栏
fun Activity.immerseStatusBar(lightMode: Boolean = true) {
    // 设置 DecorView 是否适应系统窗口
    decorFitsSystemWindows = false
    // 设置系统栏行为为通过滑动显示临时栏
    window.decorView.windowInsetsControllerCompat?.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    // 设置透明状态栏
    transparentStatusBar()
    // 设置状态栏的亮色模式
    isLightStatusBar = lightMode
    // 为内容视图添加导航栏高度的底部边距
    contentView.addNavigationBarHeightToMarginBottom()
}

// 为 Fragment 扩展一个属性，设置或获取状态栏的亮色模式
inline var Fragment.isLightStatusBar: Boolean
    get() = activity?.isLightStatusBar == true
    set(value) {
        view?.post { activity?.isLightStatusBar = value }
    }

// 为 Activity 扩展一个属性，设置或获取状态栏的亮色模式
inline var Activity.isLightStatusBar: Boolean
    get() = window.decorView.windowInsetsControllerCompat?.isAppearanceLightStatusBars == true
    set(value) {
        window.decorView.windowInsetsControllerCompat?.isAppearanceLightStatusBars = value
    }

// 为 Fragment 扩展一个属性，设置或获取状态栏颜色
inline var Fragment.statusBarColor: Int
    get() = activity?.statusBarColor ?: -1
    set(value) {
        activity?.statusBarColor = value
    }

// 为 Activity 扩展一个属性，设置或获取状态栏颜色
@setparam:ColorInt
inline var Activity.statusBarColor: Int
    get() = window.statusBarColor
    set(value) {
        window.statusBarColor = value
    }

// 为 Fragment 扩展一个函数，设置透明状态栏
fun Fragment.transparentStatusBar() {
    activity?.transparentStatusBar()
}

// 为 Activity 扩展一个函数，设置透明状态栏
fun Activity.transparentStatusBar() {
    statusBarColor = Color.TRANSPARENT
}

// 为 Fragment 扩展一个属性，设置或获取状态栏的可见性
inline var Fragment.isStatusBarVisible: Boolean
    get() = activity?.isStatusBarVisible == true
    set(value) {
        activity?.isStatusBarVisible = value
    }

// 为 Activity 扩展一个属性，设置或获取状态栏的可见性
inline var Activity.isStatusBarVisible: Boolean
    get() = window.decorView.isStatusBarVisible
    set(value) {
        window.decorView.isStatusBarVisible = value
    }

// 为 View 扩展一个属性，设置或获取状态栏的可见性
inline var View.isStatusBarVisible: Boolean
    get() = rootWindowInsetsCompat?.isVisible(Type.statusBars()) == true
    set(value) {
        windowInsetsControllerCompat?.run {
            if (value) show(Type.statusBars()) else hide(Type.statusBars())
        }
    }

// 获取状态栏的高度
val statusBarHeight: Int
    @SuppressLint("InternalInsetResource")
    get() = topActivity.window.decorView.rootWindowInsetsCompat?.getInsets(Type.statusBars())?.top
        ?: application.resources.getIdentifier("status_bar_height", "dimen", "android")
            .let { if (it > 0) application.resources.getDimensionPixelSize(it) else 0 }

// 控件的顶部外边距增加状态栏高度
fun View.addStatusBarHeightToMarginTop() = post {
    if (isStatusBarVisible && isAddedMarginTop != true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = topMargin + statusBarHeight)
            isAddedMarginTop = true
        }
    }
}

// 控件的顶部外边距减少状态栏高度
fun View.subtractStatusBarHeightToMarginTop() = post {
    if (isStatusBarVisible && isAddedMarginTop == true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = topMargin - statusBarHeight)
            isAddedMarginTop = false
        }
    }
}

// 控件的顶部内边距增加状态栏高度
fun View.addStatusBarHeightToPaddingTop() = post {
    if (isAddedPaddingTop != true) {
        updatePadding(top = paddingTop + statusBarHeight)
        updateLayoutParams {
            height = measuredHeight + statusBarHeight
        }
        isAddedPaddingTop = true
    }
}

// 控件的顶部内边距减少状态栏高度
fun View.subtractStatusBarHeightToPaddingTop() = post {
    if (isAddedPaddingTop == true) {
        updatePadding(top = paddingTop - statusBarHeight)
        updateLayoutParams {
            height = measuredHeight - statusBarHeight
        }
        isAddedPaddingTop = false
    }
}

// 判断或设置状态栏是否为浅色模式
inline var Fragment.isLightNavigationBar: Boolean
    get() = activity?.isLightNavigationBar == true
    set(value) {
        activity?.isLightNavigationBar = value
    }

// 判断或设置状态栏是否为浅色模式
inline var Activity.isLightNavigationBar: Boolean
    get() = window.decorView.windowInsetsControllerCompat?.isAppearanceLightNavigationBars == true
    set(value) {
        window.decorView.windowInsetsControllerCompat?.isAppearanceLightNavigationBars = value
    }

// 为 Fragment 扩展一个函数，设置透明导航栏
fun Fragment.transparentNavigationBar() {
    activity?.transparentNavigationBar()
}

// 为 Activity 扩展一个函数，设置透明导航栏
fun Activity.transparentNavigationBar() {
    navigationBarColor = Color.TRANSPARENT
}

// 为 Fragment 扩展一个属性，设置或获取导航栏颜色
inline var Fragment.navigationBarColor: Int
    get() = activity?.navigationBarColor ?: -1
    set(value) {
        activity?.navigationBarColor = value
    }

// 为 Activity 扩展一个属性，设置或获取导航栏颜色
inline var Activity.navigationBarColor: Int
    get() = window.navigationBarColor
    set(value) {
        window.navigationBarColor = value
    }

// 为 Fragment 扩展一个属性，设置或获取导航栏的可见性
inline var Fragment.isNavigationBarVisible: Boolean
    get() = activity?.isNavigationBarVisible == true
    set(value) {
        activity?.isNavigationBarVisible = value
    }

// 为 Activity 扩展一个属性，设置或获取导航栏的可见性
inline var Activity.isNavigationBarVisible: Boolean
    get() = window.decorView.isNavigationBarVisible
    set(value) {
        window.decorView.isNavigationBarVisible = value
    }

// 为 View 扩展一个属性，设置或获取导航栏的可见性
inline var View.isNavigationBarVisible: Boolean
    get() = rootWindowInsetsCompat?.isVisible(Type.navigationBars()) == true
    set(value) {
        windowInsetsControllerCompat?.run {
            if (value) show(Type.navigationBars()) else hide(Type.navigationBars())
        }
    }

// 获取导航栏的高度
val navigationBarHeight: Int
    @SuppressLint("InternalInsetResource")
    get() = topActivity.window.decorView.rootWindowInsetsCompat?.getInsets(Type.navigationBars())?.bottom
        ?: application.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            .let { if (it > 0) application.resources.getDimensionPixelSize(it) else 0 }


// 控件的底部外边距增加虚拟导航栏高度
fun View.addNavigationBarHeightToMarginBottom() = post {
    if (isNavigationBarVisible && isAddedMarginBottom != true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(bottom = bottomMargin + navigationBarHeight)
            isAddedMarginBottom = true
        }
    }
}

// 控件的底部外边距减少虚拟导航栏高度
fun View.subtractNavigationBarHeightToMarginBottom() = post {
    if (isNavigationBarVisible && isAddedMarginBottom == true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(bottom = bottomMargin - navigationBarHeight)
            isAddedMarginBottom = false
        }
    }
}