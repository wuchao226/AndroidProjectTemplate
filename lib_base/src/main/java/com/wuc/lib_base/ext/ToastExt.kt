package com.wuc.lib_base.ext

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
fun Fragment.toast(message: CharSequence?): Toast =
    requireActivity().toast(message)

fun Fragment.toast(@StringRes message: Int): Toast =
    requireActivity().toast(message)

fun Context.toast(message: CharSequence?): Toast =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).fixBadTokenException().apply { show() }

fun Context.toast(@StringRes message: Int): Toast =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).fixBadTokenException().apply { show() }

fun Fragment.longToast(message: CharSequence?): Toast =
    requireActivity().longToast(message)

fun Fragment.longToast(@StringRes message: Int): Toast =
    requireActivity().longToast(message)

fun Context.longToast(message: CharSequence?): Toast =
    Toast.makeText(this, message, Toast.LENGTH_LONG).fixBadTokenException().apply { show() }

fun Context.longToast(@StringRes message: Int): Toast =
    Toast.makeText(this, message, Toast.LENGTH_LONG).fixBadTokenException().apply { show() }

/**
 * 修复 7.1 的 BadTokenException
 *
 * 原因是7.1.1系统对TYPE_TOAST的Window类型做了超时限制，绑定了Window Token，最长超时时间是3.5s，
 * 如果UI在这段时间内没有执行完，Toast.show()内部的handler message得不到执行，
 * NotificationManageService那端会把这个Toast取消掉，同时把Toast对于的window token置为无效。
 * 等App端真正需要显示Toast时，因为window token已经失效，ViewRootImpl就抛出了上面的异常。
 * Android 8.0上面，google意识到这个bug，在Toast的内部加了try-catch保护。目前只有7.1.1上面的Toast存在这个问题，崩溃在系统源码里。
 */
@SuppressLint("PrivateApi")
fun Toast.fixBadTokenException(): Toast = apply {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
        try {
            @SuppressLint("DiscouragedPrivateApi")
            val tnField = Toast::class.java.getDeclaredField("mTN")
            tnField.isAccessible = true
            val tn = tnField.get(this)

            val handlerField = tnField.type.getDeclaredField("mHandler")
            handlerField.isAccessible = true
            val handler = handlerField.get(tn) as Handler

            val looper = checkNotNull(Looper.myLooper()) {
                "Can't toast on a thread that has not called Looper.prepare()"
            }
            handlerField.set(tn, object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    try {
                        handler.handleMessage(msg)
                    } catch (ignored: WindowManager.BadTokenException) {
                    }
                }
            })
        } catch (ignored: IllegalAccessException) {
        } catch (ignored: NoSuchFieldException) {
        }
    }
}