package com.wuc.ft_home.toolbar

import android.graphics.Color
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.wuc.ft_home.R
import com.wuc.lib_base.ext.toastShort
import com.wuc.lib_common.base.activity.BaseBindingReflectActivity

/**
 * @author: wuc
 * @date: 2024/10/30
 * @description: 带 MaterialToolbar Activity 业务基类
 */
abstract class ToolbarActivity<VB : ViewBinding> : BaseBindingReflectActivity<VB>() {
    protected open lateinit var mToolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbar = findViewById(R.id.toolbar)
        setListener()
        setToolbar()
    }

    protected abstract fun setToolbar()

    override fun isStatusBarEnabled(): Boolean {
        return false
    }
    private fun setListener() {
        /**
         * toolbar上back的事件处理
         */
        mToolbar.setNavigationOnClickListener {
            finish()
        }

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

    /**
     * 代码获取 ?attr/colorPrimary
     */
    protected fun getAttrColorPrimary(): Int {
        val attribute = intArrayOf(android.R.attr.colorPrimary)
        val array = this.theme.obtainStyledAttributes(attribute)
        val color = array.getColor(0, Color.TRANSPARENT)
        array.recycle()
        return color
    }
}