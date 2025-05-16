package com.wuc.lib_base.livedata.demo

import com.wuc.lib_base.livedata.ModuleLiveDataBus

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 订单模块事件定义
 */
object OrderEvents {
    // 模块名
    private const val MODULE = "order"

    // 订单状态相关事件
    object Status {
        private const val DOMAIN = "status"

        // 订单状态刷新事件
        val REFRESH = ModuleLiveDataBus.getChannel<Any>(MODULE, DOMAIN, "refresh")
    }
}