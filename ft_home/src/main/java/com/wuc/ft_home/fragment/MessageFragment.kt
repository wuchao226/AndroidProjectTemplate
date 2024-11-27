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
import com.wuc.ft_home.activity.ShapeableImageViewActivity
import com.wuc.ft_home.activity.TabLayoutActivity
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
            val item = mMessageAdapter.getData()[position]
            when (item.type) {
                MessageType.TabLayout -> openActivity<TabLayoutActivity>(requireActivity())
//                MessageType.TabLayoutCustomView -> openActivity(TabLayoutCustomViewActivity::class.java)
//                MessageType.CardView -> openActivity(CardViewActivity::class.java)
//                MessageType.BottomNavigation -> openActivity(BottomNavigationActivity::class.java)
//                MessageType.SearchView -> openActivity(SearchViewActivity::class.java)
//                MessageType.BottomSheet -> openActivity(BottomSheetActivity::class.java)
                MessageType.ShapeableImageView -> openActivity<ShapeableImageViewActivity>(requireActivity())
//                MessageType.BadgeDrawable -> openActivity(BadgeDrawableActivity::class.java)
//                MessageType.DragRecyclerView -> openActivity(DragRecyclerViewActivity::class.java)
//                MessageType.Notification -> openActivity(NotificationActivity::class.java)
//                MessageType.FloatView -> openActivity(FloatViewActivity::class.java)
//                MessageType.GuideLine -> openActivity(GuideLineActivity::class.java)
//                MessageType.Divider -> openActivity(DividerActivity::class.java)
//                MessageType.DynamicLayout -> openActivity(DynamicLayoutActivity::class.java)
                MessageType.MaterialButton -> openActivity<MaterialButtonActivity>(requireActivity())

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


    private fun getListData(): MutableList<MessageItem> {
        return mutableListOf(
            MessageItem(MessageType.TabLayout, getString(R.string.tab_layout)),
//            MessageItem(MessageType.CardView, getString(R.string.card_view)),
//            MessageItem(MessageType.BottomNavigation, getString(R.string.bottom_navigation)),
//            MessageItem(MessageType.SearchView, getString(R.string.search_view)),
//            MessageItem(MessageType.BottomSheet, getString(R.string.bottom_sheet)),
            MessageItem(MessageType.ShapeableImageView, getString(R.string.shapeable_image_view)),
//            MessageItem(MessageType.BadgeDrawable, getString(R.string.badge_drawable)),
//            MessageItem(MessageType.DragRecyclerView, getString(R.string.drag_recyclerview)),
//            MessageItem(MessageType.Notification, getString(R.string.notification)),
//            MessageItem(MessageType.FloatView, getString(R.string.float_view)),
//            MessageItem(MessageType.GuideLine, getString(R.string.guide_line)),
//            MessageItem(MessageType.Divider, getString(R.string.divider)),
//            MessageItem(MessageType.DynamicLayout, getString(R.string.dynamic_layout)),
            MessageItem(MessageType.MaterialButton, getString(R.string.material_button)),
        )
    }

    sealed class MessageType {
        data object TabLayout : MessageType()

//        data object CardView : MessageType()
//        data object BottomNavigation : MessageType()
//        data object SearchView : MessageType()
//        data object BottomSheet : MessageType()
        data object ShapeableImageView : MessageType()
//        data object BadgeDrawable : MessageType()
//        data object DragRecyclerView : MessageType()
//        data object Notification : MessageType()
//        data object FloatView : MessageType()
//        data object GuideLine : MessageType()
//        data object Divider : MessageType()
//        data object DynamicLayout : MessageType()
        data object MaterialButton : MessageType()
    }

    data class MessageItem(val type: MessageType, val title: String)

    inner class MaterialDesignAdapter : BaseRecyclerViewAdapter<MessageItem, MessageItemBinding>() {
        override fun getViewBinding(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): MessageItemBinding {
            return MessageItemBinding.inflate(layoutInflater, parent, false)
        }

        override fun onBindDefViewHolder(holder: BaseBindViewHolder<MessageItemBinding>, item: MessageItem?, position: Int) {
            if (item == null) {
                return
            }
            holder.binding.apply {
                itemTextView.text = item.title
            }
        }
    }
}