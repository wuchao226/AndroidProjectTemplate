package com.wuc.lib_base.ext.viewbinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.wuc.lib_base.R
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType

/**
 * @author: wuc
 * @date: 2024/10/23
 * @description:
 */
object ViewBindingUtil {
    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(genericOwner: Any, layoutInflater: LayoutInflater): VB =
        withGenericBindingClass<VB>(genericOwner) { clazz ->
            clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
        }.withLifecycleOwner(genericOwner)

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(genericOwner: Any, parent: ViewGroup): VB =
        inflateWithGeneric(genericOwner, LayoutInflater.from(parent.context), parent, false)

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(
        genericOwner: Any,
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): VB =
        withGenericBindingClass<VB>(genericOwner) { clazz ->
            clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                .invoke(null, layoutInflater, parent, attachToParent) as VB
        }.withLifecycleOwner(genericOwner)

    @JvmStatic
    fun <VB : ViewBinding> getBindingWithGeneric(genericOwner: Any, view: View): VB =
        view.getTag(R.id.tag_view_binding) as? VB ?: bindWithGeneric<VB>(genericOwner, view)
            .also { view.setTag(R.id.tag_view_binding, it) }

    @JvmStatic
    private fun <VB : ViewBinding> bindWithGeneric(genericOwner: Any, view: View): VB =
        withGenericBindingClass<VB>(genericOwner) { clazz ->
            clazz.getMethod("bind", View::class.java).invoke(null, view) as VB
        }

    /**
     * 将 ViewBinding 实例与生命周期所有者关联。
     * @param genericOwner 可能是 ComponentActivity 或 Fragment 的实例。
     * @return 返回 ViewBinding 实例本身，允许链式调用。
     */
    private fun <VB : ViewBinding> VB.withLifecycleOwner(genericOwner: Any) = apply {
        // 如果不支持 DataBinding，可以移除与 ViewDataBinding 相关的代码
        // 这里不需要设置 lifecycleOwner，因为 ViewBinding 不支持此功能

        // 检查 ViewBinding 是否是 ViewDataBinding 的实例
        if (this is ViewDataBinding) {
            // 如果 genericOwner 是 ComponentActivity 的实例，设置其为生命周期所有者
            if (genericOwner is ComponentActivity) {
                lifecycleOwner = genericOwner
            }
            // 如果 genericOwner 是 Fragment 的实例，设置其视图的生命周期所有者
            else if (genericOwner is Fragment) {
                lifecycleOwner = genericOwner.viewLifecycleOwner
            }
        }
    }

    /**
     * 使用泛型类来创建 ViewBinding 实例。
     * @param genericOwner 拥有泛型信息的对象，通常是一个 Activity 或 Fragment。
     * @param block 一个函数，接受一个 Class<VB> 参数并返回一个 VB 实例。
     * @return 返回通过反射创建的 ViewBinding 实例。
     */
    private fun <VB : ViewBinding> withGenericBindingClass(genericOwner: Any, block: (Class<VB>) -> VB): VB {
        // 获取 genericOwner 的直接超类的泛型类型
        var genericSuperclass = genericOwner.javaClass.genericSuperclass
        // 获取 genericOwner 的直接超类
        var superclass = genericOwner.javaClass.superclass
        // 循环遍历所有超类，直到没有超类为止
        while (superclass != null) {
            // 如果当前超类的泛型类型是参数化类型
            if (genericSuperclass is ParameterizedType) {
                // 遍历所有的泛型参数
                genericSuperclass.actualTypeArguments.forEach {
                    try {
                        // 尝试将泛型参数转换为 Class<VB> 并通过 block 函数创建实例
                        return block.invoke(it as Class<VB>)
                    } catch (e: NoSuchMethodException) {
                        // 忽略没有找到方法的异常
                    } catch (e: ClassCastException) {
                        // 忽略类型转换异常
                    } catch (e: InvocationTargetException) {
                        // 处理方法调用导致的异常
                        var tagException: Throwable? = e
                        // 循环获取真正的异常原因
                        while (tagException is InvocationTargetException) {
                            tagException = e.cause
                        }
                        // 抛出真正的异常，如果没有则抛出自定义异常
                        throw tagException
                            ?: IllegalArgumentException("ViewBinding generic was found, but creation failed.")
                    }
                }
            }
            // 更新 genericSuperclass 和 superclass 为当前超类的超类
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }
        // 如果没有找到合适的泛型参数，抛出异常
        throw IllegalArgumentException("There is no generic of ViewBinding.")
    }
}