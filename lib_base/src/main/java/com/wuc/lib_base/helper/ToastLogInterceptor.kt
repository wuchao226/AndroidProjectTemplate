package com.wuc.lib_base.helper

import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.config.IToastInterceptor
import com.luck.picture.lib.utils.ToastUtils
import com.wuc.lib_base.ext.isAppDebug
import com.wuc.lib_base.log.WLogUtils


/**
 * @author: wuc
 * @date: 2024/10/25
 * @description: 自定义 Toast 拦截器（用于追踪 Toast 调用的位置）
 */
class ToastLogInterceptor : IToastInterceptor {

    override fun intercept(params: ToastParams?): Boolean {
        if (isAppDebug) {
            // 获取调用的堆栈信息
            val stackTrace: Array<StackTraceElement> = Throwable().stackTrace
            // 跳过最前面两个堆栈
            var i = 2
            while (stackTrace.size > 2 && i < stackTrace.size) {

                // 获取代码行数
                val lineNumber: Int = stackTrace[i].lineNumber
                // 获取类的全路径
                val className: String = stackTrace[i].className
                if (((lineNumber <= 0) || className.startsWith(Toaster::class.java.name))) {
                    i++
                    continue
                }
                WLogUtils.it("Toaster", "(${stackTrace[i].fileName}:$lineNumber) ${params?.text.toString()}")
                break
                i++
            }
        }
        return false
    }
}