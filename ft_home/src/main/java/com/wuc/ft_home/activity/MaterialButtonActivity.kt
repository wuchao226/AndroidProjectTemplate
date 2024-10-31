package com.wuc.ft_home.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityMaterialButtonBinding
import com.wuc.ft_home.toolbar.ToolbarActivity

class MaterialButtonActivity : ToolbarActivity<ActivityMaterialButtonBinding>() {

    override fun setToolbar() {
        mToolbar.setTitle(getString(R.string.material_button))
    }

    override fun initView(savedInstanceState: Bundle?) {

    }
}