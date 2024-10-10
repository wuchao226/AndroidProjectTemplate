package com.wuc.lib_base.utils.mmkv

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * @author: wuc
 * @date: 2024/10/10
 * @desc: MMKV 对象的拥有者
 *
 * 用法：
 *
 * 让一个类继承 MMKVOwner 类，即可在该类使用 by mmkvXXXX() 函数将属性委托给 MMKV，例如：
 * object SettingsRepository : MMKVOwner(mmapID = "settings") {
 *   var isNightMode by mmkvBool()
 *   var language by mmkvString(default = "zh")
 * }
 * 确保每个 mmapID 不重复，只有这样才能 100% 确保类型安全！！！
 *
 * 设置或获取属性的值会调用对应的 encode() 或 decode() 函数，用属性名作为 key 值。比如：
 * if (SettingsRepository.isNightMode) {
 *   // do some thing
 * }
 * SettingsRepository.isNightMode = true
 *
 * 支持用 mmkvXXXX().asLiveData() 函数将属性委托给 LiveData，例如：
 * object SettingRepository : MMKVOwner(mmapID = "settings") {
 *   val isNightMode by mmkvBool().asLiveData()
 * }
 * SettingRepository.isNightMode.observe(this) {
 *   checkBox.isChecked = it
 * }
 *
 * SettingRepository.isNightMode.value = true
 *
 * 删除值或清理缓存：
 *  kv.removeValueForKey(::language.name) // 建议修改了默认值才移除 key，否则赋值操作更简洁
 *  kv.clearAll()
 */
interface IMMKVOwner {
    val mmapID: String
    val kv: MMKV
    fun clearAllKV() = kv.clearAll()
}

/**
 * MMKVOwner 是一个实现了 IMMKVOwner 接口的类，用于管理 MMKV 实例。
 *
 * @property mmapID String MMKV 实例的 ID，用于标识不同的 MMKV 实例。
 */
open class MMKVOwner(override val mmapID: String) : IMMKVOwner {
    /**
     * 懒加载的 MMKV 实例，使用 mmapID 进行初始化。
     *
     * @return MMKV 通过 mmapID 获取的 MMKV 实例。
     */
    override val kv: MMKV by lazy {
        // 使用 MMKV.mmkvWithID(mmapID) 方法获取 MMKV 实例，支持分区存储
        MMKV.mmkvWithID(mmapID)
    }
    // 多进程支持
    // override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.MULTI_PROCESS_MODE)
    // 加密
    // private const val CRYPT_KEY = "My-Encrypt-Key"
    //  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.SINGLE_PROCESS_MODE, CRYPT_KEY)
}

/**
 * 扩展函数，用于在 IMMKVOwner 接口中创建一个整型属性代理。
 *
 * @param default Int 属性的默认值，默认为 0。
 * @return MMKVProperty<Int> 返回一个 MMKVProperty 对象，用于代理整型属性的读写操作。
 */
fun IMMKVOwner.mmkvInt(default: Int = 0): MMKVProperty<Int> {
    // 创建并返回一个 MMKVProperty 对象
    // decode 函数：接收属性名（字符串），返回从 MMKV 中解码得到的整型值，如果没有找到则返回默认值
    // encode 函数：接收属性名和值的 Pair，将值编码并存储到 MMKV 中
    return MMKVProperty(
        decode = { kv.decodeInt(it, default) },
        encode = { kv.encode(first, second) }
    )
}

fun IMMKVOwner.mmkvLong(default: Long = 0L) =
    MMKVProperty({ kv.decodeLong(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBool(default: Boolean = false) =
    MMKVProperty({ kv.decodeBool(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvFloat(default: Float = 0f) =
    MMKVProperty({ kv.decodeFloat(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvDouble(default: Double = 0.0) =
    MMKVProperty({ kv.decodeDouble(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvString() =
    MMKVProperty({ kv.decodeString(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvString(default: String) =
    MMKVProperty({ kv.decodeString(it) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvStringSet() =
    MMKVProperty({ kv.decodeStringSet(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvStringSet(default: Set<String>) =
    MMKVProperty({ kv.decodeStringSet(it) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBytes() =
    MMKVProperty({ kv.decodeBytes(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBytes(default: ByteArray) =
    MMKVProperty({ kv.decodeBytes(it) ?: default }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable() =
    MMKVProperty({ kv.decodeParcelable(it, T::class.java) }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable(default: T) =
    MMKVProperty({ kv.decodeParcelable(it, T::class.java) ?: default }, { kv.encode(first, second) })

fun <V> MMKVProperty<V>.asLiveData() = object : ReadOnlyProperty<IMMKVOwner, MutableLiveData<V>> {
    private var cache: MutableLiveData<V>? = null

    override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): MutableLiveData<V> =
        cache ?: object : MutableLiveData<V>() {
            override fun getValue() = this@asLiveData.getValue(thisRef, property)

            override fun setValue(value: V) {
                if (super.getValue() == value) return
                this@asLiveData.setValue(thisRef, property, value)
                super.setValue(value)
            }

            override fun onActive() = super.setValue(value)
        }.also { cache = it }
}


inline fun <reified R : KProperty1<*, *>> Collection<*>.filerProperties(vararg exceptNames: String): List<R> =
    ArrayList<R>().also { destination ->
        for (element in this) if (element is R && !exceptNames.contains(element.name)) destination.add(element)
    }

/**
 * MMKVProperty 是一个自定义的属性代理类，用于将属性的读写操作委托给 MMKV 实现。
 *
 * @param V 属性的类型
 * @param decode 一个函数，用于从 MMKV 中解码属性值
 * @param encode 一个函数，用于将属性值编码并存储到 MMKV 中
 */
class MMKVProperty<V>(
    private val decode: (String) -> V, // 解码函数，接收属性名并返回属性值
    private val encode: Pair<String, V>.() -> Boolean // 编码函数，接收属性名和值的 Pair 并返回是否成功
) : ReadWriteProperty<IMMKVOwner, V> { // 实现 ReadWriteProperty 接口，指定属性拥有者为 IMMKVOwner，属性类型为 V

    /**
     * 获取属性值
     *
     * @param thisRef 属性拥有者，类型为 IMMKVOwner
     * @param property 属性的元数据，包含属性名等信息
     * @return 属性值，类型为 V
     */
    override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): V {
        // 使用解码函数，根据属性名从 MMKV 中获取属性值
        return decode(property.name)
    }

    /**
     * 设置属性值
     *
     * @param thisRef 属性拥有者，类型为 IMMKVOwner
     * @param property 属性的元数据，包含属性名等信息
     * @param value 要设置的属性值，类型为 V
     */
    override fun setValue(thisRef: IMMKVOwner, property: KProperty<*>, value: V) {
        // 使用编码函数，将属性名和值的 Pair 编码并存储到 MMKV 中
        encode((property.name) to value)
    }
}