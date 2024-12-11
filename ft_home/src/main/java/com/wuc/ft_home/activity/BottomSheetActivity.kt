package com.wuc.ft_home.activity

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityBottomSheetBinding
import com.wuc.ft_home.dialog.MyBottomSheetDialog
import com.wuc.ft_home.dialog.MyFullDialog
import com.wuc.ft_home.toolbar.ToolbarActivity

class BottomSheetActivity : ToolbarActivity<ActivityBottomSheetBinding>() {


    override fun setToolbar() {
        mToolbar.setTitle(R.string.bottom_sheet)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.btnBottomSheet.setOnClickListener {
            val behavior = BottomSheetBehavior.from(binding.llBottomSheet)
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                //如果是展开状态，则关闭，反之亦然
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.btnBottomSheetDialog.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet)
            bottomSheetDialog.show()
        }

        binding.btnBottomSheetDialogFragment.setOnClickListener {
            MyBottomSheetDialog().show(supportFragmentManager, "MyBottomSheetDialog")
        }

        binding.btnFull.setOnClickListener {
            MyFullDialog().show(supportFragmentManager, "MyFullDialog")
        }
    }
}