package com.wuc.lib_base.utils.mmkv

import android.content.Context
import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc: MMKV 工具类封装 默认的保存地址是： /data/user/0/应用包名/files/mmkv
 * 使用示例:
 * // 获取
 * MMKVUtil.get(MyConstant.TOKEN, "")// 第二个参数为默认值
 *
 * // 写入
 * MMKVUtil.put(MyConstant.TOKEN, token)
 *
 * // 清除
 * MMKVUtil.removeKey(MyConstant.TOKEN)
 *
 * // 假设 User 实现了 Parcelable
 * MMKVUtil.put("user", user)
 *
 * val user: User? = MMKVUtil.get("user", User::class.java)
 */
object MMKVUtil {
    private val mmkv: MMKV by lazy { MMKV.defaultMMKV() }

    fun init(context: Context) {
        //  App 启动时初始化 MMKV
        MMKV.initialize(context)
    }

    @JvmStatic
    fun put(key: String, value: Any) {
        when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Parcelable -> mmkv.encode(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T): T {
        if (!mmkv.containsKey(key)) {
            return defaultValue
        }
        return when (defaultValue) {
            is String -> mmkv.decodeString(key, defaultValue) as T
            is Float -> mmkv.decodeFloat(key, defaultValue) as T
            is Boolean -> mmkv.decodeBool(key, defaultValue) as T
            is Int -> mmkv.decodeInt(key, defaultValue) as T
            is Long -> mmkv.decodeLong(key, defaultValue) as T
            is Double -> mmkv.decodeDouble(key, defaultValue) as T
            is ByteArray -> mmkv.decodeBytes(key, defaultValue) as T
            is Parcelable -> mmkv.decodeParcelable(key, defaultValue.javaClass) as T
            else -> return defaultValue
        }
    }

    @JvmStatic
    fun <T : Parcelable> get(key: String, tClass: Class<T>): T? {
        if (!mmkv.containsKey(key)) {
            return null
        }
        return mmkv.decodeParcelable(key, tClass)
    }

    @JvmStatic
    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    @JvmStatic
    fun clearAll() {
        mmkv.clearAll()
    }
}