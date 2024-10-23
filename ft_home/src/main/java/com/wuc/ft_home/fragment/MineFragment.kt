package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wuc.ft_home.activity.GuideActivity
import com.wuc.ft_home.databinding.FragmentMineBinding
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_base.ext.openActivity
import com.wuc.lib_common.base.fragment.BaseBindingFragment
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
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

}