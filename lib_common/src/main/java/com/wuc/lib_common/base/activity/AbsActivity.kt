package com.wuc.lib_common.base.activity

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.wuc.lib_base.network_intercept.NetWorkMonitorManager
import com.wuc.lib_common.R
import com.wuc.lib_common.loading.LoadingUtils

/**
 * @author: wuc
 * @date: 2024/9/9
 * @desc:最底层的Activity,不带MVP和MVVM,一般不用这个
 */
abstract class AbsActivity : AppCompatActivity(), NetWorkMonitorManager.NetworkConnectionChangedListener {
    private val TAG: String
        get() = this::class.java.simpleName

    /** 状态栏沉浸 */
    private var immersionBar: ImmersionBar? = null

    private val dialogUtils by lazy {
        LoadingUtils(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout()
        initImmersionBar()
        initView(savedInstanceState)
        initData()
        if (needRegisterNetworkChangeObserver()) {
            NetWorkMonitorManager.registerObserver(this@AbsActivity)
        }
    }

    /**
     * 设置布局
     */
    open fun setContentLayout() {
        setContentView(getLayoutResId())
    }

    /**
     * 初始化视图
     * @return Int 布局id
     * 仅用于不继承BaseDataBindActivity类的传递布局文件
     */
    abstract fun getLayoutResId(): Int

    /**
     * 初始化布局
     * @param savedInstanceState Bundle?
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    open fun initData() {

    }
    /**
     * 初始化沉浸式状态栏
     */
    private fun initImmersionBar() {
        if (isStatusBarEnabled()) {
            getStatusBarConfig().init()
        }
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected open fun isStatusBarEnabled(): Boolean {
        return true
    }
    /**
     * 状态栏字体深色模式
     */
    open fun isStatusBarDarkFont(): Boolean {
        return true
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    open fun getStatusBarConfig():ImmersionBar {
        if (immersionBar == null) {
            immersionBar = createStatusBarConfig()
        }
        return immersionBar!!
    }
    /**
     * 初始化沉浸式状态栏
     */
   protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this)
            // 默认状态栏字体颜色为黑色
           .statusBarDarkFont(isStatusBarDarkFont())
            // 指定导航栏背景颜色
           .navigationBarColor(R.color.white)
            // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
           .autoDarkModeEnable(true, 0.2f)
    }

    /**
     * 加载中……弹框
     */
    fun showLoading() {
        showLoading(getString(R.string.default_loading))
    }

    /**
     * 加载提示框
     */
    fun showLoading(msg: String?) {
        dialogUtils.showLoading(msg)
    }

    /**
     * 加载提示框
     */
    fun showLoading(@StringRes res: Int) {
        showLoading(getString(res))
    }

    /**
     * 关闭提示框
     */
    fun dismissLoading() {
        dialogUtils.dismissLoading()
    }

    /**
     * 是否需要注册监听网络变换
     */
    protected open fun needRegisterNetworkChangeObserver(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needRegisterNetworkChangeObserver()) {
            NetWorkMonitorManager.unregisterObserver(this@AbsActivity)
        }
    }
}