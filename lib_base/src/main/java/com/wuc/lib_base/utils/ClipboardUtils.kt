package com.wuc.lib_base.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.wuc.lib_base.ext.application

/**
 * @author: wuc
 * @date: 2024/10/10
 * @desc: 剪切板工具类
 */
object ClipboardUtils {
    /**
     * 复制内容到剪切板
     *
     * @param text String 内容
     * @param label String 标签，用于区分内容
     */
    fun copyToClipboard(text: String, label: String = "") {
        val clipboard =
            application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}