package com.wuc.lib_base.ext

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
/**
 * 获取屏幕宽度
 */
inline val screenWidth: Int get() = application.resources.displayMetrics.widthPixels

/**
 * 获取屏幕高度
 */
inline val screenHeight: Int get() = application.resources.displayMetrics.heightPixels

/**
 * 判断或设置是否全屏
 */
inline var Fragment.isFullScreen: Boolean
    get() = activity?.isFullScreen == true
    set(value) {
        activity?.isFullScreen = value
    }

inline var Activity.isFullScreen: Boolean
    get() = window.decorView.rootWindowInsetsCompat?.isVisible(WindowInsetsCompat.Type.systemBars()) == true
    set(value) {
        window.decorView.windowInsetsControllerCompat?.run {
            val systemBars = WindowInsetsCompat.Type.systemBars()
            if (value) show(systemBars) else hide(systemBars)
        }
    }

/**
 * 判断或设置是否横屏
 */
inline var Fragment.isLandscape: Boolean
    get() = activity?.isLandscape == true
    set(value) {
        activity?.isLandscape = value
    }

inline var Activity.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    set(value) {
        requestedOrientation = if (value) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

/**
 * 判断或设置是否竖屏
 */
inline var Fragment.isPortrait: Boolean
    get() = activity?.isPortrait == true
    set(value) {
        activity?.isPortrait = value
    }

inline var Activity.isPortrait: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    set(value) {
        requestedOrientation = if (value) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }