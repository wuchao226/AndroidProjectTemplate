package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wuc.ft_home.databinding.FragmentFindBinding
import com.wuc.lib_common.base.fragment.BaseBindingFragment

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class FindFragment : BaseBindingFragment<FragmentFindBinding>() {
    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFindBinding {
        return FragmentFindBinding.inflate(inflater, container, false)
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FindFragment().apply {

            }
    }
}