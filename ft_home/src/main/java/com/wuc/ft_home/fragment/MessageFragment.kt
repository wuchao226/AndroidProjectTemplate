package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wuc.ft_home.databinding.FragmentMessageBinding
import com.wuc.lib_common.base.fragment.BaseViewBindingFragment

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class MessageFragment : BaseViewBindingFragment<FragmentMessageBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMessageBinding {
        return FragmentMessageBinding.inflate(inflater, container, false)
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            MessageFragment().apply {

            }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {

    }
}