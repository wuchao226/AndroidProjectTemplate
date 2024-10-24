package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hjq.permissions.XXPermissions
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.databinding.FragmentMessageBinding
import com.wuc.lib_base.ext.launchAppSettings
import com.wuc.lib_common.base.fragment.BaseBindingFragment
import com.wuc.lib_common.base.fragment.TitleBarFragment

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class MessageFragment : TitleBarFragment<FragmentMessageBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = MessageFragment().apply {}
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        binding.btnHomeTab.setOnClickListener {
            HomeActivity.start(requireContext(), HOME_INDEX)
        }
        binding.btnFindTab.setOnClickListener {
            HomeActivity.start(requireContext(), FIND_INDEX)
        }
        binding.btnMineTab.setOnClickListener {
            HomeActivity.start(requireContext(), MINE_INDEX)
        }
        binding.btnMessageSetting.setOnClickListener {
            // 跳转到应用详情页
            launchAppSettings()
//            XXPermissions.startPermissionActivity(requireContext())
        }
    }
}