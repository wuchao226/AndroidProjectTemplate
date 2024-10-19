package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.View
import com.wuc.ft_home.databinding.FragmentHomeBinding
import com.wuc.lib_common.base.fragment.BaseViewBindingReflectFragment

/**
  * @author: wuc
  * @date: 2024/10/18
  * @description: 首页
  */
class HomeFragment : BaseViewBindingReflectFragment<FragmentHomeBinding>() {



    override fun initView(view: View, savedInstanceState: Bundle?) {
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {

            }
    }
}