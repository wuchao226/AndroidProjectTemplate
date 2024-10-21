package com.wuc.ft_home.fragment

import androidx.annotation.IntDef

/**
 * @author: wuc
 * @date: 2024/10/21
 * @description: 主页Fragment索引
 */

const val HOME_INDEX: Int = 0
const val FIND_INDEX: Int = 1
const val MESSAGE_INDEX: Int = 2
const val MINE_INDEX: Int = 3

@IntDef(HOME_INDEX, FIND_INDEX, MESSAGE_INDEX, MINE_INDEX)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentIndex