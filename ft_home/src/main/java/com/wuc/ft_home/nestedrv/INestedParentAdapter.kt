package com.wuc.ft_home.nestedrv

/**
 * @author: wuc
 * @date: 2024/12/14
 * @description: ParentRecyclerView 的 Adapter 需实现此接口
 */
interface INestedParentAdapter {
    /**
     * 获取当前需要联动的子RecyclerView
     *
     * @return
     */
    fun getCurrentChildRecyclerView(): ChildRecyclerView?
}