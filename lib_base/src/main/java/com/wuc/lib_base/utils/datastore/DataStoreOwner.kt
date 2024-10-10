package com.wuc.lib_base.utils.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author: wuc
 * @date: 2024/10/10
 * @desc: dataStore 封装
 * 用法：
 * 让一个类继承 DataStoreOwner 类，即可在该类使用 by xxxxPreference() 函数将属性委托给 DataStore，比如：
 * object SettingsRepository : DataStoreOwner(name = "settings") {
 *   val counter by intPreference()
 *   val language by stringPreference(default = "zh")
 * }
 *
 * 调用该属性的 get() 函数会执行 dataStore.data.map {...} 的读取数据，比如：
 * // 需要在协程中调用
 * val language = SettingsRepository.language.get()
 * // val language = SettingsRepository.language.getOrDefault()
 *
 * 调用该属性的 set() 函数会执行 dataStore.edit {...} 的保存数据，比如：
 * // 需要在协程中调用
 * SettingsRepository.counter.set(100)
 * SettingsRepository.counter.set { (this ?: 0) + 1 }
 *
 * 也可以作为 Flow 或 LiveData 使用，这样每当数据发生变化都会有通知回调，可以更新 UI 或流式编程。比如：
 * SettingsRepository.counter.asLiveData()
 *   .observe(this) {
 *     tvCount.text = (it ?: 0).toString()
 *   }
 *
 * SettingsRepository.counter.asFlow()
 *   .map { ... }
 */

open class DataStoreOwner(name: String) : IDataStoreOwner {
    private val Context.dataStore by preferencesDataStore(name)
    override val dataStore get() = context.dataStore
}

interface IDataStoreOwner {
    val context: Context get() = application

    val dataStore: DataStore<Preferences>

    fun intPreference(default: Int? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Int>> =
        PreferenceProperty(::intPreferencesKey, default)

    fun doublePreference(default: Double? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Double>> =
        PreferenceProperty(::doublePreferencesKey, default)

    fun longPreference(default: Long? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Long>> =
        PreferenceProperty(::longPreferencesKey, default)

    fun floatPreference(default: Float? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Float>> =
        PreferenceProperty(::floatPreferencesKey, default)

    fun booleanPreference(default: Boolean? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Boolean>> =
        PreferenceProperty(::booleanPreferencesKey, default)

    fun stringPreference(default: String? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<String>> =
        PreferenceProperty(::stringPreferencesKey, default)

    fun stringSetPreference(default: Set<String>? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Set<String>>> =
        PreferenceProperty(::stringSetPreferencesKey, default)

    class PreferenceProperty<V>(
        private val key: (String) -> Preferences.Key<V>,
        private val default: V? = null,
    ) : ReadOnlyProperty<IDataStoreOwner, DataStorePreference<V>> {
        private var cache: DataStorePreference<V>? = null

        override fun getValue(thisRef: IDataStoreOwner, property: KProperty<*>): DataStorePreference<V> =
            cache ?: DataStorePreference(thisRef.dataStore, key(property.name), default).also { cache = it }
    }

    companion object {
        internal lateinit var application: Application
    }
}