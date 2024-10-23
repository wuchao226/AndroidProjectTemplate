package com.wuc.lib_common.base.activity

import androidx.viewbinding.ViewBinding
import com.wuc.lib_base.ext.viewbinding.ActivityBinding
import com.wuc.lib_base.ext.viewbinding.ActivityBindingDelegate

/**
 * @author: wuc
 * @date: 2024/9/9
 * @desc: 通过反射 封装ViewBinding Activity基类
 */
abstract class BaseBindingReflectActivity<VB : ViewBinding> : AbsActivity(),
    ActivityBinding<VB> by ActivityBindingDelegate() {


    override fun setContentLayout() {
        setContentViewWithBinding()
    }

    override fun getLayoutResId(): Int = 0
    override fun onDestroy() {
        super.onDestroy()
    }
}