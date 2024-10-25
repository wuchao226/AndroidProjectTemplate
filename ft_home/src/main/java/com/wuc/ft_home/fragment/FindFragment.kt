package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.FragmentFindBinding
import com.wuc.lib_common.base.fragment.BaseBindingFragment
import com.wuc.lib_common.base.fragment.TitleBarFragment
import com.wuc.lib_glide.setUrlCircle
import com.wuc.lib_glide.setUrlCircleBorder
import com.wuc.lib_glide.setUrlRound

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class FindFragment : TitleBarFragment<FragmentFindBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = FindFragment().apply {}
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 显示圆形的 ImageView
        binding.ivFindCircle.setUrlCircle(R.drawable.update_app_top_bg)
        // 显示圆角的 ImageView
        binding.ivFindCorner.setUrlRound(R.drawable.update_app_top_bg)
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

}