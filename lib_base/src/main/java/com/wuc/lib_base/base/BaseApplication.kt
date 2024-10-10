package com.wuc.lib_base.base

import android.app.Application
import com.wuc.lib_base.BuildConfig
import com.wuc.lib_base.helper.AppHelper
import com.wuc.lib_base.utils.MMKVUtil

/**
 * @author: wuc
 * @date: 2024/10/8
 * @desc: 基类的Application
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppHelper.init(this, BuildConfig.DEBUG)
        MMKVUtil.init(this)
    }
}