package com.wuc.lib_base.ext

import android.Manifest.permission.CALL_PHONE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.os.bundleOf

/**
 * @author: wuc
 * @date: 2024/10/8
 * @desc:
 */

// 创建 Intent
// 用法：intentOf<SomeOtherActivity>("id" to 5)
inline fun <reified T> Context.intentOf(vararg pairs: Pair<String, *>): Intent =
    intentOf<T>(bundleOf(*pairs))

inline fun <reified T> Context.intentOf(bundle: Bundle): Intent =
    Intent(this, T::class.java).putExtras(bundle)

/**
 * 通过 Intent 的 extras 获取可空的参数
 * 用法：
 * val name: String? by intentExtras("name", String::class)
 * val age: Int? by intentExtras("age", Int::class)
 */
fun <T> Activity.intentExtras(name: String) = lazy<T?> {
    intent.extras[name]
}


/**
 * 通过 Intent 的 extras 获取含默认值的参数
 * 用法：val count: Int by intentExtras(KEY_COUNT, default = 0)
 */
fun <T> Activity.intentExtras(name: String, default: T) = lazy {
    intent.extras[name] ?: default
}

// 通过 Intent 的 extras 获取人为保证非空的参数
/**
 * 通过 Intent 的 extras 获取人为保证非空的参数
 * 用法：val name: String by safeIntentExtras("name")
 */
fun <T> Activity.safeIntentExtras(name: String) = lazy<T> {
    checkNotNull(intent.extras[name]) { "No intent value for key \"$name\"" }
}

/**
 * 拨号（并未呼叫），无需权限
 */
fun dial(phoneNumber: String): Boolean =
    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(phoneNumber)}"))
        .startForActivity()

/**
 * 拨打电话，需要 CALL_PHONE 权限:
 */
@RequiresPermission(CALL_PHONE)
fun makeCall(phoneNumber: String): Boolean =
    Intent(Intent.ACTION_CALL, Uri.parse("tel:${Uri.encode(phoneNumber)}"))
        .startForActivity()

// 发短信
fun sendSMS(phoneNumber: String, content: String): Boolean =
    Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${Uri.encode(phoneNumber)}"))
        .putExtra("sms_body", content)
        .startForActivity()

// 打开网页
fun browse(url: String, newTask: Boolean = false): Boolean =
    Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .apply { if (newTask) newTask() }
        .startForActivity()

// 发送邮件
fun email(email: String, subject: String? = null, text: String? = null): Boolean =
    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        .putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        .putExtra(Intent.EXTRA_SUBJECT, subject)
        .putExtra(Intent.EXTRA_TEXT, text)
        .startForActivity()

// 安装 APK
fun installAPK(uri: Uri): Boolean =
    Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, "application/vnd.android.package-archive")
        .newTask()
        .grantReadUriPermission()
        .startForActivity()

fun Intent.startForActivity(): Boolean =
    try {
        topActivity.startActivity(this)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

// 添加 FLAG_ACTIVITY_CLEAR_TASK 的 flag
fun Intent.clearTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

fun Intent.clearTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

fun Intent.newDocument(): Intent = addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

fun Intent.excludeFromRecents(): Intent = addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

fun Intent.multipleTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

fun Intent.newTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

fun Intent.noAnimation(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

fun Intent.noHistory(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

fun Intent.singleTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

fun Intent.grantReadUriPermission(): Intent = apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}