package com.wuc.lib_base.ext

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
import android.content.res.Resources
import android.util.TypedValue


inline val Int.dp: Float get() = toFloat().dp

inline val Long.dp: Float get() = toFloat().dp

inline val Double.dp: Float get() = toFloat().dp

/**
 * dp 转 px
 */
inline val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

inline val Int.sp: Float get() = toFloat().sp

inline val Long.sp: Float get() = toFloat().sp

inline val Double.sp: Float get() = toFloat().sp

/**
 * sp 转 px
 */
inline val Float.sp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

/**
 * px 转 dp
 */
fun Int.pxToDp(): Int = toFloat().pxToDp()

fun Long.pxToDp(): Int = toFloat().pxToDp()

fun Double.pxToDp(): Int = toFloat().pxToDp()

fun Float.pxToDp(): Int =
    (this / Resources.getSystem().displayMetrics.density + 0.5f).toInt()

/**
 * px 转 sp
 */
fun Int.pxToSp(): Int = toFloat().pxToSp()

fun Long.pxToSp(): Int = toFloat().pxToSp()

fun Double.pxToSp(): Int = toFloat().pxToSp()

fun Float.pxToSp(): Int =
    (this / Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()


/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
fun dip2px(dpValue: Float): Int {
//    val scale = context.resources.displayMetrics.density
//    return (dpValue * scale + 0.5f).toInt()
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        Resources.getSystem().displayMetrics
    ).toInt()
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun px2dip(pxValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
//    val scale = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
 */
fun px2sp(pxValue: Float): Int {
    val scaledDensity = Resources.getSystem().displayMetrics.scaledDensity
    return (pxValue / scaledDensity + 0.5f).toInt()
}

/**
 * 根据手机的分辨率从 sp(缩放独立像素) 的单位 转成为 px(像素)
 */
fun sp2px(spValue: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spValue,
        Resources.getSystem().displayMetrics
    ).toInt()
//    val scaledDensity = Resources.getSystem().displayMetrics.scaledDensity
//    return (spValue * scaledDensity + 0.5f).toInt()
}

