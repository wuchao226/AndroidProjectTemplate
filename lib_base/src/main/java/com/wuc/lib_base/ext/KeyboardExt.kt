package com.wuc.lib_base.ext

import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
/**
 * 显示键盘
 */
fun View.showKeyboard() = windowInsetsControllerCompat?.show(Type.ime())

/**
 * 隐藏键盘
 */
fun View.hideKeyboard() = windowInsetsControllerCompat?.hide(Type.ime())

/**
 * 切换键盘显示状态
 */
fun View.toggleKeyboard() = if (isKeyboardVisible) hideKeyboard() else showKeyboard()

/**
 * 判断键盘是否可见
 */
inline var View.isKeyboardVisible: Boolean
    get() = rootWindowInsetsCompat?.isVisible(Type.ime()) == true
    set(value) {
        if (value) showKeyboard() else hideKeyboard()
    }

/**
 * 获取键盘高度
 */
inline val View.keyboardHeight: Int
    get() = rootWindowInsetsCompat?.getInsets(Type.ime())?.bottom ?: -1