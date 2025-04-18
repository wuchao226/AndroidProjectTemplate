package com.wuc.lib_base.ext

import com.wuc.lib_base.log.WLogUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter

/**
 * @author: wuc
 * @date: 2025/4/11
 * @description:
 */
internal fun Throwable.getFullStackTrace(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    printStackTrace(pw)
    var cause = this.cause
    while (cause != null && cause != this) {
        pw.println("\nCaused by: ${cause.stackTraceToString()}")
        cause = cause.cause
    }
    pw.flush()
    return sw.toString()
}

private fun createExceptionHandler(): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, throwable ->
        val exceptionType = throwable::class.java.name
        val exceptionMessage = throwable.message ?: "no exception information"
        WLogUtils.et(
            "James_Coroutine_Exception",
            """
                exception_type: $exceptionType
                message: $exceptionMessage
                stack_trace:
                ${throwable.getFullStackTrace()}
            """.trimIndent()
        )
    }
}

private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + createExceptionHandler())
private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + createExceptionHandler())
private val defaultScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + createExceptionHandler())

fun launchMain(block: suspend CoroutineScope.() -> Unit) = mainScope.launch { block() }
fun launchIO(block: suspend CoroutineScope.() -> Unit) = ioScope.launch { block() }
fun launchDefault(block: suspend CoroutineScope.() -> Unit) = defaultScope.launch { block() }