package com.wuc.lib_common.base.activity

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * @author: wuc
 * @date: 2024/9/13
 * @desc:基于 ViewBinding 和 ViewModel Activity 基类
 */
abstract class BaseBindingVMActivity<VB : ViewBinding, VM : ViewModel> : BaseBindingActivity<VB>() {

    protected lateinit var mViewModel: VM

    /**
     * 通过 lazy 初始化 ViewModel，子类可以覆盖此函数实现特定的 ViewModelProvider 逻辑
     */
    protected open val viewModelProvider: () -> VM = {
        ViewModelProvider(this)[getViewModelClass()]
    }

    /**
     * 子类需要提供 ViewModel 的具体类型
     * 示例：
     * override fun getViewModelClass(): Class<ExampleViewModel> = ExampleViewModel::class.java
     */
    protected abstract fun getViewModelClass(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = viewModelProvider()
        super.onCreate(savedInstanceState)
    }
}