package com.wuc.lib_base.ext.viewbinding

import android.view.View
import androidx.viewbinding.ViewBinding
import com.wuc.lib_base.R

/**
 * @author: wuc
 * @date: 2024/10/19
 * @description: View Bind 扩展
 */

inline fun <reified VB : ViewBinding> View.getBinding() = getBinding(VB::class.java)

@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> View.getBinding(clazz: Class<VB>) =
    getTag(R.id.tag_view_binding) as? VB ?: (clazz.getMethod("bind", View::class.java)
        .invoke(null, this) as VB).also { setTag(R.id.tag_view_binding, it) }
