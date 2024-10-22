package com.wuc.lib_base.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import java.util.LinkedList

/**
 * @author: wuc
 * @date: 2024/10/8
 * @desc:
 */
internal val activityCache = LinkedList<Activity>()

// 获取栈顶 Activity
val topActivity: Activity get() = activityCache.last()

// 获取 Activity 栈链表
val activityList: List<Activity> get() = activityCache.toList()

val topActivityOrNull: Activity? get() = activityCache.lastOrNull()

val topActivityOrApplication: Context get() = topActivityOrNull ?: application


fun startActivity(intent: Intent) = topActivity.startActivity(intent)

/**
 * 启动 Activity
 * 用法: [Activity].startActivity<SomeOtherActivity>("id" to 5)
 */
inline fun <reified T : Activity> startActivity(
    vararg pairs: Pair<String, Any?>,
    crossinline block: Intent.() -> Unit = {}
) =
    topActivity.startActivity<T>(pairs = pairs, block = block)

inline fun <reified T : Activity> Context.startActivity(
    vararg pairs: Pair<String, Any?>,
    crossinline block: Intent.() -> Unit = {}
) =
    startActivity(intentOf<T>(*pairs).apply(block))

/**
 * openActivity<TestActivity>(context) {
 *     putExtra("param1", "data")
 *     putExtra("param2", 123)
 * }
 */
inline fun <reified T> openActivity(context: Context, noinline block: (Intent.() -> Unit)? = null) {
    val intent = Intent(context, T::class.java)
    block?.invoke(intent)
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

/**
 * 关闭 Activity 并返回结果
 * 用法: Activity.finishWithResult("id" to 5, "name" to name)
 */
fun Activity.finishWithResult(vararg pairs: Pair<String, *>) {
    setResult(Activity.RESULT_OK, Intent().putExtras(bundleOf(*pairs)))
    finish()
}

/**
 * 判断 Activity 是否存在栈中
 * 用法: isActivityExistsInStack<SomeOtherActivity>
 */
inline fun <reified T : Activity> isActivityExistsInStack(): Boolean =
    isActivityExistsInStack(T::class.java)

fun <T : Activity> isActivityExistsInStack(clazz: Class<T>): Boolean =
    activityCache.any { it.javaClass.name == clazz.name }

/**
 * 结束某个 Activity
 * 用法: finishActivity<SomeOtherActivity>
 */
inline fun <reified T : Activity> finishActivity(): Boolean = finishActivity(T::class.java)

fun <T : Activity> finishActivity(clazz: Class<T>): Boolean =
    activityCache.removeAll {
        if (it.javaClass.name == clazz.name) it.finish()
        it.javaClass.name == clazz.name
    }

/**
 * 结束到某个 Activity
 * 用法: finishToActivity<SomeOtherActivity>
 */
inline fun <reified T : Activity> finishToActivity(): Boolean = finishToActivity(T::class.java)

fun <T : Activity> finishToActivity(clazz: Class<T>): Boolean {
    for (i in activityCache.count() - 1 downTo 0) {
        if (clazz.name == activityCache[i].javaClass.name) {
            return true
        }
        activityCache.removeAt(i).finish()
    }
    return false
}

/**
 * 结束所有 Activity
 */
fun finishAllActivities(): Boolean =
    activityCache.removeAll {
        it.finish()
        true
    }

/**
 * 结束除某个 Activity 之外的所有 Activity
 * 用法: finishAllActivitiesExcept<SomeOtherActivity>
 */
inline fun <reified T : Activity> finishAllActivitiesExcept(): Boolean =
    finishAllActivitiesExcept(T::class.java)

fun <T : Activity> finishAllActivitiesExcept(clazz: Class<T>): Boolean =
    activityCache.removeAll {
        if (it.javaClass.name != clazz.name) it.finish()
        it.javaClass.name != clazz.name
    }

/**
 * 结束除最新 Activity 之外的所有 Activity
 */
fun finishAllActivitiesExceptNewest(): Boolean =
    finishAllActivitiesExcept(topActivity.javaClass)

/**
 * 双击返回退出 App
 * 用法: ComponentActivity.pressBackTwiceToExitApp(toastText, [delayMillis])
 */
fun ComponentActivity.pressBackTwiceToExitApp(
    toastText: String,
    delayMillis: Long = 2000,
    owner: LifecycleOwner = this
) =
    pressBackTwiceToExitApp(delayMillis, owner) { toast(toastText) }

fun ComponentActivity.pressBackTwiceToExitApp(
    @StringRes toastText: Int,
    delayMillis: Long = 2000,
    owner: LifecycleOwner = this
) =
    pressBackTwiceToExitApp(delayMillis, owner) { toast(toastText) }

/**
 * 双击返回退出 App，自定义 Toast
 * 用法: ComponentActivity.pressBackTwiceToExitApp([delayMillis]) {...}
 */
fun ComponentActivity.pressBackTwiceToExitApp(
    delayMillis: Long = 2000,
    owner: LifecycleOwner = this,
    onFirstBackPressed: () -> Unit
) = onBackPressedDispatcher.addCallback(owner, object : OnBackPressedCallback(true) {
    private var lastBackTime: Long = 0
    override fun handleOnBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackTime > delayMillis) {
            onFirstBackPressed()
            lastBackTime = currentTime
        } else {
            finishAllActivities()
        }
    }
})

/**
 * 点击返回不退出 App
 * 用法: ComponentActivity.pressBackToNotExit()
 */
fun ComponentActivity.pressBackToNotExitApp(owner: LifecycleOwner = this) =
    doOnBackPressed(owner) { moveTaskToBack(false) }

/**
 * 禁用返回按钮
 * 用法: ComponentActivity.pressBackToNotExit()
 */
fun ComponentActivity.disableBackPressed(owner: LifecycleOwner = this) =
    doOnBackPressed(owner) { return@doOnBackPressed }

/**
 * 监听手机的返回事件
 * 用法: ComponentActivity.doOnBackPressed {...}
 */
fun ComponentActivity.doOnBackPressed(owner: LifecycleOwner = this, onBackPressed: () -> Unit) =
    onBackPressedDispatcher.addCallback(owner, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = onBackPressed()
    })

/**
 * 判断是否有权限
 */
fun Context.isPermissionGranted(permission: String): Boolean =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.arePermissionsGranted(vararg permissions: String): Boolean =
    permissions.all { isPermissionGranted(it) }


/**
 * 获取布局的根视图
 */
inline val Activity.contentView: View
    get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)

/**
 * 把 Context 作为 Activity 使用
 */
fun Context.asActivity(): Activity? =
    this as? Activity ?: (this as? ContextWrapper)?.baseContext?.asActivity()

/**
 * 作用域的 this 不是 Activity 时获取 Context
 */
inline val Context.context: Context get() = this

/**
 * 作用域的 this 不是 Activity 时获取 Activity
 */
inline val Activity.activity: Activity get() = this

/**
 * 作用域的 this 不是 Activity 时获取 FragmentActivity
 */
inline val FragmentActivity.fragmentActivity: FragmentActivity get() = this

/**
 * 作用域的 this 不是 Activity 时获取 LifecycleOwner
 */
inline val ComponentActivity.lifecycleOwner: LifecycleOwner get() = this

/**
 * 扩展属性，用于设置或获取 Activity 的 decorFitsSystemWindows 属性
 * 
 * @property decorFitsSystemWindows Boolean
 * 当设置为 true 时，系统窗口装饰（如状态栏和导航栏）将适应系统窗口插图。
 * 当设置为 false 时，系统窗口装饰将不会适应系统窗口插图。
 * 
 * @throws UnsupportedOperationException 如果尝试获取该属性的值，将抛出异常。
 */
var Activity.decorFitsSystemWindows: Boolean
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR)
    get() = noGetter() // 获取该属性的值时抛出异常，因为不支持获取操作
    set(value) = WindowCompat.setDecorFitsSystemWindows(window, value) // 设置该属性的值

