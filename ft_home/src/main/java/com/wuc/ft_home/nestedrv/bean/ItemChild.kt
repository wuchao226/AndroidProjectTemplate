package com.wuc.ft_home.nestedrv.bean

import com.wuc.lib_common.interfaces.MultiItemEntity

/**
 * @author: wuc
 * @date: 2024/12/16
 * @description:
 */
data class ItemChild(
    override val itemType: Int,
    val title: String = "",
) : MultiItemEntity
