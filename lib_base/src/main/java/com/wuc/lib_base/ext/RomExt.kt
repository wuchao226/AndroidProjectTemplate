package com.wuc.lib_base.ext

import android.os.Build

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
inline val isXiaomiRom: Boolean get() = isRomOf("xiaomi")

inline val isHuaweiRom: Boolean get() = isRomOf("huawei")

inline val isOppoRom: Boolean get() = isRomOf("oppo")

inline val isVivoRom: Boolean get() = isRomOf("vivo")

inline val isOnePlusRom: Boolean get() = isRomOf("oneplus")

/**
 * 判断是否是锤子 Rom
 */
inline val isSmartisanRom: Boolean get() = isRomOf("smartisan", "deltainno")

inline val isMeiZuRom: Boolean get() = isRomOf("meizu")

inline val isSamsungRom: Boolean get() = isRomOf("samsung")

inline val isGoogleRom: Boolean get() = isRomOf("google")

inline val isSonyRom: Boolean get() = isRomOf("sony")

/**
 * 判断是否是某个 Rom
 */
fun isRomOf(vararg names: String): Boolean =
    names.any { it.contains(Build.BRAND, true) || it.contains(Build.MANUFACTURER, true) }

/**
 * 判断是否是鸿蒙系统
 */
val isHarmonyOS: Boolean
    get() {
        try {
            val clazz = Class.forName("com.huawei.system.BuildEx")
            val classLoader = clazz.classLoader
            if (classLoader != null && classLoader.parent == null) {
                return clazz.getMethod("getOsBrand").invoke(clazz) == "harmony"
            }
        } catch (e: ClassNotFoundException) {
        } catch (e: NoSuchMethodException) {
        } catch (e: Exception) {
        }
        return false
    }