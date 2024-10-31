package com.wuc.ft_home.toolbar

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.wuc.ft_home.R
import com.wuc.lib_base.ext.toastShort
import com.wuc.lib_common.base.fragment.BaseBindingReflectFragment
import com.wuc.lib_common.base.fragment.TitleBarFragment

/**
 * @author: wuc
 * @date: 2024/10/31
 * @description:  带 MaterialToolbar Fragment 业务基类
 */
abstract class ToolbarFragment<VB : ViewBinding> : TitleBarFragment<VB>() {
    protected open lateinit var mToolbar: MaterialToolbar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mToolbar = view.findViewById(R.id.toolbar)
        setListener()
        setToolbar()
    }
    protected abstract fun setToolbar()
    private fun setListener() {
        /**
         * toolbar上back的事件处理
         */
        mToolbar.navigationIcon = null

        /**
         * toolbar上menu的事件处理
         */
        mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_author -> {
                    Snackbar
                        .make(binding.root, "作者：wuc", Snackbar.LENGTH_LONG)
                        .setAction("记住了") {
                            toastShort("祝你一夜暴富")
                        }
                        .show()
                }

                R.id.menu_share -> {
                    toastShort("分享")
                }

                R.id.menu_settings -> {
                    toastShort("设置")
                }
            }
            return@setOnMenuItemClickListener true
        }
    }
}