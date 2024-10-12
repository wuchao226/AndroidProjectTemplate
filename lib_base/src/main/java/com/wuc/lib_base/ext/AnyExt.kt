package com.wuc.lib_base.ext

/**
  * @author: wuc
  * @date: 2024/10/11
  * @desc:
  */
inline fun <reified T> Any.saveAs(): T {
    return this as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.saveAsUnChecked(): T {
    return this as T
}

inline fun <reified T> Any.isEqualType(): Boolean {
    return this is T
}