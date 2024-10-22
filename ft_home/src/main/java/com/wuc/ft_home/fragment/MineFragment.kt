package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wuc.ft_home.activity.GuideActivity
import com.wuc.ft_home.databinding.FragmentMineBinding
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_base.ext.openActivity
import com.wuc.lib_common.base.fragment.BaseViewBindingFragment

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class MineFragment : BaseViewBindingFragment<FragmentMineBinding>() {
    companion object {
        @JvmStatic
        fun newInstance() =
            MineFragment().apply {}
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMineBinding {
        return FragmentMineBinding.inflate(inflater, container, false)
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding.btnMineGuide.doOnDebouncingClick {
            openActivity<GuideActivity>(requireContext())
        }
    }

}