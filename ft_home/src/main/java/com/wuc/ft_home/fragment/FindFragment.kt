package com.wuc.ft_home.fragment

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.FragmentFindBinding
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_base.ext.toast
import com.wuc.lib_common.base.fragment.TitleBarFragment
import com.wuc.lib_common.widget.view.SimpleRatingBar
import com.wuc.lib_common.widget.view.SwitchButton
import com.wuc.lib_glide.setUrlCircle
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

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 显示圆形的 ImageView
        binding.ivFindCircle.setUrlCircle(R.drawable.update_app_top_bg)
        // 显示圆角的 ImageView
        binding.ivFindCorner.setUrlRound(R.drawable.update_app_top_bg)
        binding.sbFindSwitch.setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener{
            override fun onCheckedChanged(button: SwitchButton, checked: Boolean) {
                toast(checked)
            }
        })

        binding.cvFindCountdown.doOnDebouncingClick {
            // 倒计时示例
            toast(R.string.common_code_send_hint)
            binding.cvFindCountdown.start()
        }
        binding.ratingBar.setOnRatingBarChangeListener(object : SimpleRatingBar.OnRatingChangeListener{
            override fun onRatingChanged(ratingBar: SimpleRatingBar, grade: Float, touch: Boolean) {
                toast("$grade  $touch")
            }
        })
    }

}