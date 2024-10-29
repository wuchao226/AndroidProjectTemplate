package com.wuc.ft_home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.FragmentListBinding
import com.wuc.lib_common.base.fragment.BaseBindingReflectFragment

/**
  * @author: wuc
  * @date: 2024/10/29
  * @description: 加载案例 Fragment
  */
class ListFragment : BaseBindingReflectFragment<FragmentListBinding>() {


    companion object {
        @JvmStatic
        fun newInstance() =
            ListFragment().apply {}
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {

    }
}