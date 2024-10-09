package com.wuc.lib_base.ext

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
internal const val NO_GETTER: String = "Property does not have a getter"

internal fun noGetter(): Nothing = throw NotImplementedError(NO_GETTER)