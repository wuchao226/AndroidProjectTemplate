package com.wuc.lib_base.ext

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
/**
 * 根据 id 获取字符串
 */
fun View.getString(@StringRes id: Int): String =
    context.getString(id)

/**
 * 根据 id 获取数字
 */
fun Fragment.getDimension(@DimenRes id: Int): Float =
    requireContext().getDimension(id)

fun View.getDimension(@DimenRes id: Int): Float =
    context.getDimension(id)

fun Context.getDimension(@DimenRes id: Int): Float =
    resources.getDimension(id)

/**
 * 根据 id 获取颜色
 */
@ColorInt
fun Fragment.getCompatColor(@ColorRes id: Int): Int =
    requireContext().getCompatColor(id)

@ColorInt
fun View.getCompatColor(@ColorRes id: Int): Int =
    context.getCompatColor(id)

@ColorInt
fun Context.getCompatColor(@ColorRes id: Int): Int =
    ResourcesCompat.getColor(resources, id, null)

/**
 * 根据 id 获取 Drawable
 */
fun Fragment.getCompatDrawable(@DrawableRes id: Int): Drawable? =
    requireContext().getCompatDrawable(id)

fun View.getCompatDrawable(@DrawableRes id: Int): Drawable? =
    context.getCompatDrawable(id)

fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable? =
    ResourcesCompat.getDrawable(resources, id, null)

fun getCompatDrawable(@DrawableRes id: Int): Drawable? =
    ContextCompat.getDrawable(application.applicationContext, id)

/**
 * 根据 id 获取字体
 */
fun Fragment.getCompatFont(@FontRes id: Int): Typeface? =
    requireContext().getCompatFont(id)

fun View.getCompatFont(@FontRes id: Int): Typeface? =
    context.getCompatFont(id)

fun Context.getCompatFont(@FontRes id: Int): Typeface? =
    ResourcesCompat.getFont(this, id)
