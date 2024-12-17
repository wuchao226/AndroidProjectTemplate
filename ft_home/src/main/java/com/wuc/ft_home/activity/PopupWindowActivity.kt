package com.wuc.ft_home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityPopupWindowBinding
import com.wuc.ft_home.databinding.PopupItemBinding
import com.wuc.ft_home.toolbar.ToolbarActivity
import com.wuc.lib_base.ext.toast
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter
import com.wuc.lib_common.popup.PopWindow

class PopupWindowActivity : ToolbarActivity<ActivityPopupWindowBinding>(), PopWindow.ViewInterface {
    private var mPopLRWindow: PopWindow? = null
    private var mPopAllWindow: PopWindow? = null
    private var mPopDownWindow: PopWindow? = null
    override fun setToolbar() {
        mToolbar.setTitle(R.string.PopupWindow)
    }

    override fun initView(savedInstanceState: Bundle?) {
        //向下弹出
        binding.showDownPop.setOnClickListener {
            showDownPop(binding.showDownPop)
        }
    }

    //向下弹出
    fun showDownPop(view: View) {
        mPopDownWindow = PopWindow.Builder(this)
            .setView(R.layout.popup_down)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimDown)
            .setChildrenView(this)
            .create()
        mPopDownWindow?.showOnTargetBottom(view)

        //得到button的左上角坐标
//        int[] positions = new int[2];
//        view.getLocationOnScreen(positions);
//        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.NO_GRAVITY, 0, positions[1] + view.getHeight());
    }

    //向右弹出
    fun showRightPop(view: View) {
        mPopLRWindow = PopWindow.Builder(this)
            .setView(R.layout.popup_left_or_right)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimHorizontal)
            .setChildrenView(this)
            .create()
        mPopLRWindow?.showOnTargetRight(view)

        //得到button的左上角坐标
//        int[] positions = new int[2];
//        view.getLocationOnScreen(positions);
//        mPopWindow.showAtLocation(findViewById(android.R.id.content), Gravity.NO_GRAVITY, positions[0] + view.getWidth(), positions[1]);
    }

    //向左弹出
    fun showLeftPop(view: View) {
        mPopLRWindow = PopWindow.Builder(this)
            .setView(R.layout.popup_left_or_right)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setAnimStyle(R.style.AnimRight)
            .setChildrenView(this)
            .create()
        mPopLRWindow?.showOnTargetLeft(view)

        //得到button的左上角坐标
//        int[] positions = new int[2];
//        view.getLocationOnScreen(positions);
//        mPopWindow.showAtLocation(findViewById(android.R.id.content), Gravity.NO_GRAVITY, positions[0] - popupWindow.getWidth(), positions[1]);
    }

    //全屏弹出
    fun showAll(view: View) {
        mPopAllWindow = PopWindow.Builder(this)
            .setView(R.layout.popup_up)
            .setWidthAndHeight(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            .setBackGroundLevel(0.5f) //取值范围0.0f-1.0f 值越小越暗
            .setAnimStyle(R.style.AnimUp)
            .setChildrenView(this)
            .create()
        mPopAllWindow?.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0)
    }

    //向右上弹出
    fun showUpPop(view: View) {
        PopWindow.Builder(this)
            .setView(R.layout.query_info2)
            .setChildrenView(this)
            .create()
            .showOnTargetTop(view, PopWindow.RIGHT_TOP, hExtra = -20)

        //得到button的左上角坐标
//        int[] positions = new int[2];
//        view.getLocationOnScreen(positions);
//        mPopWindow.showAtLocation(findViewById(android.R.id.content),
//                Gravity.NO_GRAVITY, positions[0], positions[1] - popupWindow.getHeight());
    }


    //正上方弹出
    fun showCenterTop(view: View) {
        PopWindow.Builder(this)
            .setView(R.layout.query_info2)
            .setChildrenView(this)
            .setOutsideTouchable(false)
            .create()
            .showOnTargetTop(view, PopWindow.CENTER_TOP, hExtra = -20)
    }

    fun showReminder(view: View) {
        PopWindow.Builder(this)
            .setView(R.layout.query_info)
            .create()
            .showOnTargetTop(view, PopWindow.LEFT_TOP, 20, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getChildView(view: View, layoutResId: Int, pop: PopupWindow) {
        //获得PopupWindow布局里的View
        when (layoutResId) {
            R.layout.popup_down -> {
                val recycle_view = view.findViewById<View>(R.id.recycle_view) as RecyclerView
                recycle_view.layoutManager = GridLayoutManager(this, 3)
                val mAdapter = PopupAdapter()
                recycle_view.adapter = mAdapter
                val list = ArrayList<String>()
                for (i in 0 until 15) {
                    list.add("王者农药")
                }
                mAdapter.setData(list)
                mAdapter.onItemClickListener = { view, position ->
                    toast("position is $position")
                    mPopDownWindow?.dismiss()
                }
            }

            R.layout.popup_up -> {
                val btn_take_photo = view.findViewById<View>(R.id.btn_take_photo) as Button
                val btn_select_photo = view.findViewById<View>(R.id.btn_select_photo) as Button
                val btn_cancel = view.findViewById<View>(R.id.btn_cancel) as Button
                btn_take_photo.setOnClickListener {
                    toast("拍照")
                    mPopAllWindow?.dismiss()
                }
                btn_select_photo.setOnClickListener {
                    toast("相册选取")
                    mPopAllWindow?.dismiss()
                }
                btn_cancel.setOnClickListener {
                    mPopAllWindow?.dismiss()
                }
                view.setOnTouchListener { v, event ->
                    mPopAllWindow?.dismiss()
                    true
                }
            }

            R.layout.popup_left_or_right -> {
                val tv_like = view.findViewById<View>(R.id.tv_like) as TextView
                val tv_hate = view.findViewById<View>(R.id.tv_hate) as TextView
                tv_like.setOnClickListener {
                    toast("赞一个")
                    mPopLRWindow?.dismiss()
                }
                tv_hate.setOnClickListener {
                    toast("踩一下")
                    mPopLRWindow?.dismiss()
                }
            }
        }
    }

    inner class PopupAdapter : BaseRecyclerViewAdapter<String, PopupItemBinding>() {
        override fun onBindDefViewHolder(holder: BaseBindViewHolder<PopupItemBinding>, item: String?, position: Int) {
            if (item == null) {
                return
            }
            holder.binding.choiceText.text = item
        }

        override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): PopupItemBinding {
            return PopupItemBinding.inflate(layoutInflater, parent, false)
        }

    }
}