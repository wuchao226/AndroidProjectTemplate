package com.wuc.lib_base.ext

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import kotlin.math.roundToInt

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
/**
 * 获取文本字符串
 */
inline val TextView.textString: String get() = text.toString()

/**
 * 判断文本是否为空
 */
fun TextView.isTextEmpty(): Boolean = textString.isEmpty()

/**
 * 判断文本是否非空
 */
fun TextView.isTextNotEmpty(): Boolean = textString.isNotEmpty()

/**
 * 判断或设置密码是否可见
 */
inline var TextView.isPasswordVisible: Boolean
    get() = transformationMethod != PasswordTransformationMethod.getInstance()
    set(value) {
        transformationMethod = if (value) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
    }

inline var TextView.isFakeBoldText: Boolean
    get() = paint.isFakeBoldText
    set(value) {
        paint.isFakeBoldText = value
    }

var TextView.font: Int
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR)
    get() = noGetter()
    set(@FontRes value) {
        typeface = context.getCompatFont(value)
    }

var TextView.typefaceFromAssets: String
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR)
    get() = noGetter()
    set(value) {
        typeface = Typeface.createFromAsset(context.assets, value)
    }

/**
 * 添加下划线
 */
fun TextView.addUnderline() {
    paint.flags = Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.transparentHighlightColor() {
    highlightColor = Color.TRANSPARENT
}

/**
 * 开启倒计时并修改文字
 * TextView.startCountDown(lifecycleOwner, [secondInFuture], onTick, onFinish)
 * btnSendCode.startCountDown(this,
 *   onTick = {
 *     text = "${it} second"
 *   },
 *   onFinish = {
 *     text = "send"
 *   })
 */
fun TextView.startCountDown(
    lifecycleOwner: LifecycleOwner,
    secondInFuture: Int = 60,
    onTick: TextView.(secondUntilFinished: Int) -> Unit,
    onFinish: TextView.() -> Unit,
): CountDownTimer =
    object : CountDownTimer(secondInFuture * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            isEnabled = false
            onTick((millisUntilFinished / 1000f).roundToInt())
        }

        override fun onFinish() {
            isEnabled = true
            this@startCountDown.onFinish()
        }
    }.also { countDownTimer ->
        countDownTimer.start()
        lifecycleOwner.doOnLifecycle(onDestroy = {
            countDownTimer.cancel()
        })
    }

/**
 * 当其它文本非空时按钮可点击
 * 用法：
 * 设置按钮在输入框有内容时才能点击：
 * btnLogin.enableWhenOtherTextNotEmpty(edtAccount, edtPwd)
 */
fun TextView.enableWhenOtherTextNotEmpty(vararg textViews: TextView) =
    enableWhenOtherTextChanged(*textViews) { all { it.isTextNotEmpty() } }

/**
 * 当其它文本改变时按钮可点击
 */
inline fun TextView.enableWhenOtherTextChanged(
    vararg textViews: TextView,
    crossinline block: Array<out TextView>.() -> Boolean
) {
    isEnabled = block(textViews)
    textViews.forEach { tv ->
        tv.doAfterTextChanged {
            isEnabled = block(textViews)
        }
    }
}

/**
 * 当选项全选中时按钮可点击
 */
fun TextView.enableWhenAllChecked(vararg checkBoxes: CheckBox) {
    isEnabled = checkBoxes.all { it.isChecked }
    checkBoxes.forEach { cb ->
        cb.setOnCheckedChangeListener { _, _ ->
            isEnabled = checkBoxes.all { it.isChecked }
        }
    }
}