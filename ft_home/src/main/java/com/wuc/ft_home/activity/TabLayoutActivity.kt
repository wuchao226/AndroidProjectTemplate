package com.wuc.ft_home.activity

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.google.android.material.tabs.TabLayout
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityTabLayoutBinding
import com.wuc.ft_home.databinding.ItemTabBinding
import com.wuc.ft_home.databinding.TabItemBinding
import com.wuc.ft_home.dialog.CompanyDialog
import com.wuc.ft_home.fragment.tablayout.AndroidFragment
import com.wuc.ft_home.fragment.tablayout.FlutterFragment
import com.wuc.ft_home.fragment.tablayout.KotlinFragment
import com.wuc.ft_home.toolbar.ToolbarActivity
import com.wuc.lib_base.ext.design.FragmentStateExtAdapter
import com.wuc.lib_base.ext.design.addOnTabSelectedListener
import com.wuc.lib_base.ext.design.doOnCustomTabSelected
import com.wuc.lib_base.ext.design.setCustomView
import com.wuc.lib_base.ext.design.setupWithViewPager2
import com.wuc.lib_base.ext.viewbinding.getBinding

/**
 * @author: wuc
 * @date: 2024/11/25
 * @description:
 */
class TabLayoutActivity : ToolbarActivity<ActivityTabLayoutBinding>() {
    private val tabTitles = mutableListOf("Android", "Kotlin", "Flutter")
    private val tabFragments = mutableListOf(
        AndroidFragment.newInstance(), KotlinFragment.newInstance(), FlutterFragment.newInstance()
    )
    private var defaultIndex = 0

    override fun setToolbar() {
        mToolbar.setTitle(R.string.tab_layout)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initViewPager()

        initTabLayout1()//基础样式
        initTabLayout2()//添加icon & 去掉indicator
        initTabLayout3()//style 字体大小、加粗 & 顶部indicator
        initTabLayout4()//下划线样式 & tab分割线
        initTabLayout5()//Badge 数字 & 红点
        initTabLayout6()//TabLayout样式 & 选中字体加粗
        initTabLayout7()//获取隐藏tab
        initTabLayout8()//自定义item view & lottie
        initTabLayout9()//自定义Custom view
    }

    private fun initViewPager() {
        binding.viewPager2.adapter = FragmentStateExtAdapter(tabFragments.size) { position ->
            tabFragments[position]
        }
    }

    /**
     * 基础样式
     */
    private fun initTabLayout1() {
        //tabLayout关联viewpager
        binding.tabLayout1.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }
        //设置默认选中
        binding.tabLayout1.getTabAt(defaultIndex)?.select()
    }

    /**
     * 添加icon & 去掉indicator
     */
    private fun initTabLayout2() {
        binding.tabLayout2.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }
        for (index in 0..binding.tabLayout2.tabCount) {
            binding.tabLayout2.getTabAt(index)?.setIcon(R.drawable.material_logo)
        }
    }

    /**
     * style 字体大小、加粗 & 顶部indicator
     */
    private fun initTabLayout3() {
        binding.tabLayout3.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }
    }

    /**
     * 下划线样式 & tab分割线
     */
    private fun initTabLayout4() {
        binding.tabLayout4.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }
        //设置 分割线
        for (index in 0..binding.tabLayout4.tabCount) {
            val linearLayout = binding.tabLayout4.getChildAt(index) as? LinearLayout
            linearLayout?.let {
                it.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                it.dividerDrawable = ContextCompat.getDrawable(this, R.drawable.shape_tab_divider)
                it.dividerPadding = 30
            }
        }
    }

    /**
     * Badge 数字 & 红点
     */
    private fun initTabLayout5() {
        binding.tabLayout5.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }
        //Badge 数字
        binding.tabLayout5.getTabAt(defaultIndex)?.let { tab ->
            tab.orCreateBadge.apply {
                number = 999
                backgroundColor = Color.RED
                maxCharacterCount = 3
                badgeTextColor = Color.WHITE
            }
        }
        //红点
        binding.tabLayout5.getTabAt(1)?.let { tab ->
            tab.orCreateBadge.apply {
                backgroundColor = ContextCompat.getColor(this@TabLayoutActivity, com.wuc.lib_common.R.color.common_cancel_text_color)
            }
        }
    }

    /**
     * TabLayout样式 & 选中字体加粗
     */
    private fun initTabLayout6() {
        binding.tabLayout6.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }
        binding.tabLayout6.addOnTabSelectedListener(
            onTabSelected = { tab ->
                switchTextStyle(tab)
            },
            onTabUnselected = { tab ->
                switchTextStyle(tab)
            }
        )
    }

    private fun switchTextStyle(tab: TabLayout.Tab?) {
        tab?.let {
            val textView = it.view.getChildAt(1) as TextView
            textView.typeface = Typeface.defaultFromStyle(if (it.isSelected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private val companyMap = mapOf("苹果" to 2, "亚马逊" to 0, "谷歌" to 8, "微软" to 0, "阿里巴巴" to 0, "腾讯" to 0, "脸书" to 0, "三星" to 0, "思科" to 2)

    /**
     * 隐藏tab count红点提示function & tab宽度
     */
    private fun initTabLayout7() {
        companyMap.forEach {
            val tab = binding.tabLayout7.newTab()
            tab.text = it.key
            tab.orCreateBadge.apply {
                backgroundColor = Color.RED
                maxCharacterCount = 3
                number = it.value
                badgeTextColor = Color.WHITE
                isVisible = it.value > 0
            }

            hideToolTipText(tab)

            binding.tabLayout7.addTab(tab)
        }

        // mBinding.tabLayout7.setOnScrollChangeListener() // min api 23 (6.0)
        // 适配 5.0  滑动过程中判断右侧小红点是否需要显示
        binding.tabLayout7.viewTreeObserver.addOnScrollChangedListener {
            binding.vArrowDot.visibility = if (isShowDot()) View.VISIBLE else View.INVISIBLE
        }

        binding.ivArrow.setOnClickListener {
            CompanyDialog(companyMap, binding.tabLayout7.selectedTabPosition).show(supportFragmentManager, "CompanyDialog")
        }
    }

    /**
     * 隐藏长按显示文本
     */
    private fun hideToolTipText(tab: TabLayout.Tab) {
        // 取消长按事件
        tab.view.isLongClickable = false
        // api 26 以上 设置空text
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            tab.view.tooltipText = ""
        }
    }

    override fun onResume() {
        super.onResume()
        // 初始化判断右侧小红点是否需要显示
        binding.tabLayout7.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.vArrowDot.visibility = if (isShowDot()) View.VISIBLE else View.INVISIBLE
                binding.tabLayout7.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private var lastShowIndex = 0// 最后一个可见tab

    private fun isShowDot(): Boolean {
        var showIndex = 0
        var tipCount = 0
        companyMap.keys.forEachIndexed { index, _ ->
            binding.tabLayout7.getTabAt(index)?.let { tab ->
                val tabView = tab.view as LinearLayout
                val rect = Rect()
                val visible = tabView.getLocalVisibleRect(rect)
                // 可见范围小于80%也在计算范围之内，剩下20%宽度足够红点透出（可自定义）
                if (visible && rect.right > tab.view.width * 0.8) {
                    showIndex = index
                } else {
                    //if (index > showIndex) // 任意一个有count的tab隐藏就会显示，比如第一个在滑动过程中会隐藏，也在计算范围之内
                    if (index > lastShowIndex) { // 只检测右侧隐藏且有count的tab 才在计算范围之内
                        tab.badge?.let { tipCount += it.number }
                    }
                }

            }
        }
        lastShowIndex = showIndex
        return tipCount > 0
    }


    data class LottieBean(val name: String, val rawId: Int)

    /**
     * 自定义item view & lottie
     */
    private fun initTabLayout8() {
        val animList = mutableListOf(
            LottieBean("party", R.raw.anim_confetti),
            LottieBean("pizza", R.raw.anim_pizza),
            LottieBean("apple", R.raw.anim_apple)
        )

        binding.tabLayout8.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.setCustomView<ItemTabBinding> {
                lavTabImg.setAnimation(animList[position].rawId)
                lavTabImg.setColorFilter(Color.BLUE)
                tvTabText.text = animList[position].name
            }
        }
        binding.tabLayout8.addOnTabSelectedListener(
            onTabSelected = { tab ->
                tab.setSelected()
            },
            onTabUnselected = { tab ->
                tab.setUnselected()
            }
        )
        val defaultTab = binding.tabLayout8.getTabAt(0)
        defaultTab?.select()
        defaultTab?.setSelected()
    }

    /**
     * 选中状态
     */
    fun TabLayout.Tab.setSelected() {
        this.customView?.let {
            val textView = it.findViewById<TextView>(R.id.tv_tab_text)
            val selectedColor = ContextCompat.getColor(this@TabLayoutActivity, com.wuc.lib_common.R.color.common_accent_color)
            textView.setTextColor(selectedColor)

            val imageView = it.findViewById<LottieAnimationView>(R.id.lav_tab_img)
            if (!imageView.isAnimating) {
                imageView.playAnimation()
            }
            setLottieColor(imageView, true)
        }
    }

    /**
     * 未选中状态
     */
    fun TabLayout.Tab.setUnselected() {
        this.customView?.let {
            val textView = it.findViewById<TextView>(R.id.tv_tab_text)
            val unselectedColor = ContextCompat.getColor(this@TabLayoutActivity, R.color.black)
            textView.setTextColor(unselectedColor)

            val imageView = it.findViewById<LottieAnimationView>(R.id.lav_tab_img)
            if (imageView.isAnimating) {
                imageView.cancelAnimation()
                imageView.progress = 0f // 还原初始状态
            }
            setLottieColor(imageView, false)
        }
    }

    /**
     * set lottie icon color
     */
    private fun setLottieColor(imageView: LottieAnimationView?, isSelected: Boolean) {
        imageView?.let {
            val color = if (isSelected) com.wuc.lib_common.R.color.common_accent_color else R.color.black
            val csl = AppCompatResources.getColorStateList(this@TabLayoutActivity, color)
            val filter = SimpleColorFilter(csl.defaultColor)
            val keyPath = KeyPath("**")
            val callback = LottieValueCallback<ColorFilter>(filter)
            it.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
        }
    }


    data class TabTimeBean(val name: String, val time: String)

    /**
     * 自定义Custom view
     */
    private fun initTabLayout9() {
        val tabTimeList = mutableListOf(
            TabTimeBean("已开抢", "22:00"),
            TabTimeBean("秒杀中", "22:00"),
            TabTimeBean("即将开始", "22:00")
        )
        binding.tabLayout9.setupWithViewPager2(binding.viewPager2) { tab, position ->
            tab.setCustomView<TabItemBinding> {
                tabItemName.text = tabTimeList[position].name
                tabItemTime.text = tabTimeList[position].time
            }
            handleTabSelected(tab, position == 0)
        }
        binding.tabLayout9.doOnCustomTabSelected<TabItemBinding>(
            onTabSelected = { tab ->
                handleTabSelected(tab, true)
            },
            onTabUnselected = { tab ->
                handleTabSelected(tab, false)
            }
        )
    }
    private fun handleTabSelected(tab: TabLayout.Tab, isSelected: Boolean) {
        val tabItemBinding = tab.customView?.getBinding<TabItemBinding>()
        if (isSelected) {
            tabItemBinding?.tabItemName?.isSelected = true
            tabItemBinding?.tabItemTime?.isSelected = true
            tabItemBinding?.tabItemName?.textSize = 12f
            tabItemBinding?.tabItemTime?.textSize = 18f
        } else {
            tabItemBinding?.tabItemName?.isSelected = false
            tabItemBinding?.tabItemTime?.isSelected = false
            tabItemBinding?.tabItemName?.textSize = 12f
            tabItemBinding?.tabItemTime?.textSize = 12f
        }
    }
}