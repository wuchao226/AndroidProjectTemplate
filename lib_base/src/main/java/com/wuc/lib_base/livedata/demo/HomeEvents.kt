package com.wuc.lib_base.livedata.demo

import com.wuc.lib_base.livedata.ModuleLiveDataBus

/**
 * @author: wuc
 * @date: 2025/5/16
 * @description: 首页模块事件定义
 */
object HomeEvents {
    // 模块名
    private const val MODULE = "home"

    // 用户信息相关事件
    object UserProfile {
        private const val DOMAIN = "user_profile"

        // 用户信息更新事件
        val UPDATE = ModuleLiveDataBus.getChannel<Any>(MODULE, DOMAIN, "update")
    }

    // 列表相关事件
    object List {
        private const val DOMAIN = "list"

        // 列表刷新事件
        val REFRESH = ModuleLiveDataBus.getChannel<Any>(MODULE, DOMAIN, "refresh")
    }
}