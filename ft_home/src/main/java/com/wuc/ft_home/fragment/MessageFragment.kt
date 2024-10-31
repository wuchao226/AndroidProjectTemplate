package com.wuc.ft_home.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import com.gyf.immersionbar.ImmersionBar
import com.wuc.ft_home.R
import com.wuc.ft_home.activity.MaterialButtonActivity
import com.wuc.ft_home.databinding.FragmentMessageBinding
import com.wuc.ft_home.databinding.MessageItemBinding
import com.wuc.ft_home.toolbar.ToolbarFragment
import com.wuc.lib_base.ext.doOnDebouncingClick
import com.wuc.lib_base.ext.isAppDarkMode
import com.wuc.lib_base.ext.openActivity
import com.wuc.lib_common.adapter.BaseBindViewHolder
import com.wuc.lib_common.adapter.BaseRecyclerViewAdapter
import com.wuc.lib_common.base.fragment.TitleBarFragment

/**
 * @author: wuc
 * @date: 2024/10/18
 * @description:
 */
class MessageFragment : ToolbarFragment<FragmentMessageBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = MessageFragment().apply {}
    }

    private lateinit var mMessageAdapter: MaterialDesignAdapter
    override fun setToolbar() {
        mToolbar.setTitle(getString(R.string.message_title))
        mToolbar.isTitleCentered = true
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        ImmersionBar.setTitleBar(requireActivity(), binding.include.toolbar)
        //回调刷新toolbar的menu，页面初始化或者在需要的时候调用
        getAttachActivity()?.invalidateOptionsMenu()
        mMessageAdapter = MaterialDesignAdapter()
        binding.recyclerview.apply {
            adapter = mMessageAdapter
        }
        mMessageAdapter.setData(getListData())
        binding.floatingButton.doOnDebouncingClick {
            if (isAppDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        initFloatingButtonImage()

        mMessageAdapter.onItemClickListener = { view, position ->
            when (position) {
//                0 -> openActivity(SwipeRefreshLayoutActivity::class.java)
//                1 -> openActivity(FloatingActionButtonActivity::class.java)
//                2 -> openActivity(SnackbarActivity::class.java)
//                3 -> openActivity(TabLayoutActivity::class.java)
//                4 -> openActivity(TabLayoutCustomViewActivity::class.java)
//                5 -> openActivity(CardViewActivity::class.java)
//                6 -> openActivity(BottomNavigationActivity::class.java)
//                7 -> openActivity(CollapsingToolbarActivity::class.java)
//                8 -> openActivity(TextInputLayoutActivity::class.java)
//                9 -> openActivity(SearchViewActivity::class.java)
//                10 -> openActivity(DrawerLayoutActivity::class.java)
//                11 -> openActivity(BottomSheetActivity::class.java)
                12 -> openActivity<MaterialButtonActivity>(requireActivity())
//                13 -> openActivity(ShapeableImageViewActivity::class.java)
//                14 -> openActivity(BadgeDrawableActivity::class.java)
//                15 -> openActivity(DragRecyclerViewActivity::class.java)
//                16 -> openActivity(NotificationActivity::class.java)
//                17 -> openActivity(FloatViewActivity::class.java)
//                18 -> openActivity(GuideLineActivity::class.java)
//                19 -> openActivity(DividerActivity::class.java)
//                20 -> openActivity(DynamicLayoutActivity::class.java)
            }
        }
    }

    /**
     * 修改主题后会重建，初始化显示icon
     */
    private fun initFloatingButtonImage() {
        if (isAppDarkMode) {
            binding.floatingButton.setImageResource(R.mipmap.ic_day)
        } else {
            binding.floatingButton.setImageResource(R.mipmap.ic_night)
        }
    }


    private fun getListData(): MutableList<String> {
        return mutableListOf(
            getString(R.string.swipe_refresh_layout),
            getString(R.string.floating_action_button),
            getString(R.string.snack_bar),
            getString(R.string.tab_layout),
            getString(R.string.tab_layout_custom_view),
            getString(R.string.card_view),
            getString(R.string.bottom_navigation),
            getString(R.string.collapsing_toolbar),
            getString(R.string.text_input_layout),
            getString(R.string.search_view),
            getString(R.string.drawer_layout),
            getString(R.string.bottom_sheet),
            getString(R.string.material_button),
            getString(R.string.shapeable_image_view),
            getString(R.string.badge_drawable),
            getString(R.string.drag_recyclerview),
            getString(R.string.notification),
            getString(R.string.float_view),
            getString(R.string.guide_line),
            getString(R.string.divider),
            getString(R.string.dynamic_layout)

        )
    }

    inner class MaterialDesignAdapter : BaseRecyclerViewAdapter<String, MessageItemBinding>() {
        override fun getViewBinding(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): MessageItemBinding {
            return MessageItemBinding.inflate(layoutInflater, parent, false)
        }

        override fun onBindDefViewHolder(holder: BaseBindViewHolder<MessageItemBinding>, item: String?, position: Int) {
            if (item == null) {
                return
            }
            holder.binding.apply {
                itemTextView.text = item
            }
        }
    }
}