package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.View
import com.wuc.ft_home.HomeActivity
import com.wuc.ft_home.activity.GuideActivity
import com.wuc.ft_home.databinding.FragmentMineBinding
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_base.ext.launchAppSettings
import com.wuc.lib_base.ext.openActivity
import com.wuc.lib_common.base.fragment.TitleBarFragment

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class MineFragment : TitleBarFragment<FragmentMineBinding>() {
    companion object {
        @JvmStatic
        fun newInstance() =
            MineFragment().apply {}
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        binding.btnMineGuide.doOnDebouncingClick {
            openActivity<GuideActivity>(requireContext())
        }
        binding.btnMineCrash.doOnDebouncingClick {
            // 上报错误到 Bugly 上
//            CrashReport.postCatchedException(IllegalStateException("are you ok?"))
            // 关闭 Bugly 异常捕捉
//            CrashReport.closeBugly()
            throw IllegalStateException("are you ok?")
        }
        binding.btnHomeTab.setOnClickListener {
            HomeActivity.start(requireContext(), HOME_INDEX)
        }
        binding.btnFindTab.setOnClickListener {
            HomeActivity.start(requireContext(), FIND_INDEX)
        }

        binding.btnMessageSetting.setOnClickListener {
            // 跳转到应用详情页
            launchAppSettings()
//            XXPermissions.startPermissionActivity(requireContext())
        }
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

}