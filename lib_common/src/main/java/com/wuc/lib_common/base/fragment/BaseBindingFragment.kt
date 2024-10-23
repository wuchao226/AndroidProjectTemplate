package com.wuc.lib_common.base.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * @author: wuc
 * @date: 2024/9/13
 * @desc: ViewBinding Fragment 基类
 */
abstract class BaseBindingFragment<VB : ViewBinding> : AbsFragment() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = requireNotNull(_binding) { "The property of binding has been destroyed." }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        // 调用子类实现的 createViewBinding 方法
        _binding = createViewBinding(inflater, container)
        return binding.root
    }

    /**
     * 创建 ViewBinding 实例
     * 示例：
     * override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentExampleBinding {
     *     return FragmentExampleBinding.inflate(inflater, container, false)
     * }
     */
    protected abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    override fun getLayoutResId(): Int = 0
    override fun onDestroyView() {
        super.onDestroyView()
        // 视图销毁时清理 ViewBinding 对象
        _binding = null
    }
}