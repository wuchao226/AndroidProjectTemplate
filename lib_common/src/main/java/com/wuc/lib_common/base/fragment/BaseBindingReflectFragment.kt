package com.wuc.lib_common.base.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.wuc.lib_base.ext.saveAs
import com.wuc.lib_base.ext.saveAsUnChecked
import com.wuc.lib_base.ext.viewbinding.FragmentBinding
import com.wuc.lib_base.ext.viewbinding.FragmentBindingDelegate
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * @author: wuc
 * @date: 2024/9/13
 * @desc: 通过反射 封装ViewBinding Fragment基类
 */
abstract class BaseBindingReflectFragment<VB : ViewBinding> : AbsFragment(),
    FragmentBinding<VB> by FragmentBindingDelegate() {

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return createViewWithBinding(inflater, container)
    }
    override fun getLayoutResId(): Int = 0
}