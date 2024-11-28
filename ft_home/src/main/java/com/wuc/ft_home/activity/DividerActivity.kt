package com.wuc.ft_home.activity

import android.os.Bundle
import android.view.View
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityDividerBinding
import com.wuc.ft_home.toolbar.ToolbarActivity

class DividerActivity : ToolbarActivity<ActivityDividerBinding>() {

    private var isShow = true
    override fun setToolbar() {
        mToolbar.setTitle(R.string.divider)
    }

    override fun initView(savedInstanceState: Bundle?) {
        // for android:animateLayoutChanges="true"
        binding.btnVisible.setOnClickListener {
            if (isShow) {
                binding.tvAbout.visibility = View.GONE
            } else {
                binding.tvAbout.visibility = View.VISIBLE
            }
            isShow = !isShow
        }
    }
}