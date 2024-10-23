package com.wuc.lib_common.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.wuc.lib_base.network_intercept.NetWorkMonitorManager
import com.wuc.lib_common.action.BundleAction
import com.wuc.lib_common.base.activity.AbsActivity

/**
 * @author: wuc
 * @date: 2024/9/12
 * @desc: Fragment 基类
 */
abstract class AbsFragment : Fragment(), BundleAction, NetWorkMonitorManager.NetworkConnectionChangedListener {

    protected var TAG: String? = this::class.java.simpleName

    /** Activity 对象 */
    private var activity: AbsActivity? = null

    /** 根布局 */
    private var rootView: View? = null

    /** 当前是否加载过 */
    private var loading: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() is AbsActivity) {
            activity = requireActivity() as AbsActivity
        } else {
            throw IllegalStateException("Activity must be subclass of AbsActivity")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (needRegisterNetworkChangeObserver()) {
            NetWorkMonitorManager.registerObserver(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loading = false
        rootView = getContentView(inflater, container)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!loading) {
            loading = true
            initData()
            onFragmentResume(true)
            return
        }
        if (this.activity?.lifecycle?.currentState == Lifecycle.State.STARTED) {
            onActivityResume()
        } else {
            onFragmentResume(false)
        }
    }

    override fun getView(): View? {
        return rootView
    }

    /**
     * 设置布局View
     */
    open fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getLayoutResId(), null)
    }

    /**
     * 初始化视图
     * @return Int 布局id
     * 仅用于不继承BaseDataBindFragment类的传递布局文件
     */
    abstract fun getLayoutResId(): Int

    /**
     * 初始化布局
     * @param savedInstanceState Bundle?
     */
    abstract fun initView(view: View, savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    open fun initData() {}

    /**
     * Fragment 可见回调
     *
     * @param first                 是否首次调用
     */
    protected open fun onFragmentResume(first: Boolean) {}

    /**
     * Activity 可见回调
     */
    protected open fun onActivityResume() {}

    /**
     * 获取绑定的 Activity，防止出现 getActivity 为空
     */
    open fun getAttachActivity(): AbsActivity? {
        return activity
    }

    /**
     * 这个 Fragment 是否已经加载过了
     */
    open fun isLoading(): Boolean {
        return loading
    }

    override fun getBundle(): Bundle? {
        return arguments
    }

    /**
     * 加载中……弹框
     */
    fun showLoading() {
        getAttachActivity()?.showLoading()
    }

    /**
     * 加载提示框
     */
    fun showLoading(msg: String?) {
        getAttachActivity()?.showLoading(msg)
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
        getAttachActivity()?.dismissLoading()
    }

    /**
     * 是否需要注册监听网络变换
     */
    protected open fun needRegisterNetworkChangeObserver(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        loading = false
        if (needRegisterNetworkChangeObserver()) {
            NetWorkMonitorManager.unregisterObserver(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }
}