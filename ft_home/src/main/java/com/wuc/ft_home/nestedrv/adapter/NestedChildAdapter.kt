package com.wuc.ft_home.nestedrv.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.wuc.lib_common.adapter.ViewPage2FragmentAdapter


/**
 * @author: wuc
 * @date: 2024/12/16
 * @description:
 */
class NestedChildAdapter(fragmentActivity: FragmentActivity, fragmentArray : SparseArray<Fragment>)
    : ViewPage2FragmentAdapter(fragmentActivity.supportFragmentManager, fragmentActivity.lifecycle, fragmentArray) {

}