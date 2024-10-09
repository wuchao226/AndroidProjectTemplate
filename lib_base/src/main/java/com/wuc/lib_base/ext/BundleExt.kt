package com.wuc.lib_base.ext

import android.os.Bundle

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
@Suppress("UNCHECKED_CAST")
operator fun <T> Bundle?.get(key: String): T? = this?.get(key) as? T