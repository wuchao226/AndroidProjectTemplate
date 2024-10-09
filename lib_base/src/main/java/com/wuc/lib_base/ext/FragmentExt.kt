package com.wuc.lib_base.ext

import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
/**
 * 创建 Fragment 时伴随参数
 * Fragment.withArguments("id" to 5)
 */
fun <T : Fragment> T.withArguments(vararg pairs: Pair<String, *>) = apply {
    arguments = bundleOf(*pairs)
}

/**
 * 通过 Fragment 的 arguments 获取可空的参数
 * val name: String? by arguments(KEY_NAME)
 */
fun <T > Fragment.arguments(key: String) = lazy<T?> {
    arguments[key]
}

/**
 * 通过 Fragment 的 arguments 获取含默认值的参数
 * val count: Int by arguments(KEY_COUNT, default = 0)
 */
fun <T> Fragment.arguments(key: String, default: T) = lazy {
    arguments[key] ?: default
}

/**
 * 通过 Fragment 的 arguments 获取人为保证非空的参数
 * val phone: String by safeArguments(KEY_PHONE)
 */
fun <T> Fragment.safeArguments(key: String) = lazy<T> {
    checkNotNull(arguments[key]) { "No intent value for key \"$key\"" }
}

fun Fragment.pressBackTwiceToExitApp(toastText: String, delayMillis: Long = 2000) =
    requireActivity().pressBackTwiceToExitApp(toastText, delayMillis, viewLifecycleOwner)

fun Fragment.pressBackTwiceToExitApp(@StringRes toastText: Int, delayMillis: Long = 2000) =
    requireActivity().pressBackTwiceToExitApp(toastText, delayMillis, viewLifecycleOwner)

fun Fragment.pressBackTwiceToExitApp(delayMillis: Long = 2000, onFirstBackPressed: () -> Unit) =
    requireActivity().pressBackTwiceToExitApp(delayMillis, viewLifecycleOwner, onFirstBackPressed)

fun Fragment.pressBackToNotExitApp() =
    requireActivity().pressBackToNotExitApp(viewLifecycleOwner)

fun Fragment.doOnBackPressed(onBackPressed: () -> Unit) =
    requireActivity().doOnBackPressed(viewLifecycleOwner, onBackPressed)