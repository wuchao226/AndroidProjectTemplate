package com.wuc.ft_home.activity

import android.os.Bundle
import android.view.View
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityFloatViewBinding
import com.wuc.ft_home.toolbar.ToolbarActivity
import com.wuc.ft_home.widgets.AvatarFloatView
import com.wuc.lib_base.ext.toast
import com.wuc.lib_common.float.FloatManager
import com.wuc.lib_common.widget.view.BaseFloatView
import com.wuc.lib_common.widget.view.FloatAdsorbType

class FloatViewActivity : ToolbarActivity<ActivityFloatViewBinding>() {
    private var mFloatView: AvatarFloatView? = null
    override fun setToolbar() {
        mToolbar.setTitle(getString(R.string.float_view))
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.btnShowFloat.setOnClickListener {
            mFloatView = AvatarFloatView(this)
            mFloatView?.setDragDistance(0.3)
            if (binding.radioLeftRight.isChecked) {
                mFloatView?.setAdsorbType(FloatAdsorbType.AdsorbHorizontal)
            } else {
                mFloatView?.setAdsorbType(FloatAdsorbType.AdsorbVertical)
            }
//            FloatManager.with(this).add(mFloatView!!).show()

            FloatManager.with(this).add(AvatarFloatView(this))
                .setClick(object : BaseFloatView.OnFloatClickListener {
                    override fun onClick(view: View) {
//                        Toast.makeText(this@FloatViewActivity, "click", Toast.LENGTH_SHORT).show()
                        toast("click")
                    }
                })
                .show()
        }
        binding.btnHideFloat.setOnClickListener {
            FloatManager.hide()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_left_right) {
                mFloatView?.setAdsorbType(FloatAdsorbType.AdsorbHorizontal)
            } else {
                mFloatView?.setAdsorbType(FloatAdsorbType.AdsorbVertical)
            }
        }
    }
}