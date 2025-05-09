package com.wuc.ft_home.nestedrv.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityNestStickBinding
import com.wuc.ft_home.nestedrv.bean.ItemParent
import com.wuc.ft_home.nestedrv.adapter.NestedParentAdapter
import com.wuc.ft_home.toolbar.ToolbarActivity
import com.wuc.lib_base.ext.dp


/**
 * @author: wuc
 * @date: 2024/12/15
 * @description: RecyclerView 嵌套滑动置顶
 * RecyclerView嵌套滑动置顶 项目应用篇:https://juejin.cn/post/6941996743974191111#heading-7
 * Android Tab吸顶 嵌套滚动通用实现方案:https://juejin.cn/post/7312338839695081499#heading-3
 */
class NestStickActivity : ToolbarActivity<ActivityNestStickBinding>() {
    // 广告是否关闭
    private var floatAdClosed = false
    override fun setToolbar() {
        mToolbar.setTitle(R.string.recyclerview_nested)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(this)
        val parentAdapter = NestedParentAdapter(this)
        binding.parentRecyclerView.adapter = parentAdapter

        parentAdapter.setData(
            listOf(
                ItemParent(itemType = NestedParentAdapter.TYPE_ITEM, R.drawable.p1),
                ItemParent(itemType = NestedParentAdapter.TYPE_ITEM, R.drawable.p2),
                ItemParent(itemType = NestedParentAdapter.TYPE_ITEM, R.drawable.p3),
                ItemParent(itemType = NestedParentAdapter.TYPE_INNER, -1)
            )
        )
        binding.parentRecyclerView.setStickyListener {
            if (!floatAdClosed) {
                if (it) {
                    // 置顶了
                    binding.homeFloatLayout.visibility = View.VISIBLE
                } else {
                    // 没有置顶
                    binding.homeFloatLayout.visibility = View.GONE
                }
            }
        }
        if (!floatAdClosed) {
            // 设置悬浮布局的高度
            binding.parentRecyclerView.setStickyHeight(50f.dp.toInt())
        }
        binding.tvText.setOnClickListener {
            binding.homeFloatLayout.visibility = View.GONE
            floatAdClosed = true
            binding.parentRecyclerView.setStickyHeight(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_nested_stick, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_tab) {
//            val intent = Intent(this, MainActivityWithoutTab::class.java)
//            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}