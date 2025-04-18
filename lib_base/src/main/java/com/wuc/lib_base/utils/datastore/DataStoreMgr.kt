package com.wuc.lib_base.utils.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wuc.lib_base.ext.application
import com.wuc.lib_base.ext.launchIO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author: wuc
 * @date: 2025/4/16
 * @description: DataStore 工具类封装
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "datastore")

sealed class DataStoreValue<T> {
    abstract val key: Preferences.Key<T>
    abstract val defaultValue: T

    data class IntValue(override val key: Preferences.Key<Int>, override val defaultValue: Int) : DataStoreValue<Int>()
    data class LongValue(override val key: Preferences.Key<Long>, override val defaultValue: Long) : DataStoreValue<Long>()
    data class DoubleValue(override val key: Preferences.Key<Double>, override val defaultValue: Double) : DataStoreValue<Double>()
    data class FloatValue(override val key: Preferences.Key<Float>, override val defaultValue: Float) : DataStoreValue<Float>()
    data class BooleanValue(override val key: Preferences.Key<Boolean>, override val defaultValue: Boolean) : DataStoreValue<Boolean>()
    data class StringValue(override val key: Preferences.Key<String>, override val defaultValue: String) : DataStoreValue<String>()
    data class ByteArrayValue(override val key: Preferences.Key<ByteArray>, override val defaultValue: ByteArray) : DataStoreValue<ByteArray>()
}

class DataStoreMgr private constructor() {
    companion object {
        private const val TAG = "DataStoreMgr"

        @Volatile
        private var instance: DataStoreMgr? = null
        fun get(): DataStoreMgr = instance ?: synchronized(this) {
            instance ?: DataStoreMgr().also { instance = it }
        }
    }

    suspend fun <T> get(key: String, defaultValue: T): T {
        val preferenceKey = createPreferenceKey(key, defaultValue)
        return application.dataStore.data.map { it[preferenceKey.key] ?: defaultValue }.first()
    }

    fun <T> put(key: String, value: T) {
        launchIO {
            val preferenceKey = createPreferenceKey(key, value)
            application.dataStore.edit { preferences ->
                preferences[preferenceKey.key] = value
            }
        }
    }

    fun removeKey(key: String) {
        launchIO {
            application.dataStore.edit { preferences ->
                preferences.asMap().keys
                    .filter { it.name == key }
                    .forEach { preferences.remove(it) }
            }
        }
    }

    fun clear() {
        launchIO {
            application.dataStore.edit { it.clear() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> createPreferenceKey(key: String, value: T): DataStoreValue<T> {
        return when (value) {
            is Int -> DataStoreValue.IntValue(intPreferencesKey(key), value)
            is Long -> DataStoreValue.LongValue(longPreferencesKey(key), value)
            is Double -> DataStoreValue.DoubleValue(doublePreferencesKey(key), value)
            is Float -> DataStoreValue.FloatValue(floatPreferencesKey(key), value)
            is Boolean -> DataStoreValue.BooleanValue(booleanPreferencesKey(key), value)
            is String -> DataStoreValue.StringValue(stringPreferencesKey(key), value)
            is ByteArray -> DataStoreValue.ByteArrayValue(byteArrayPreferencesKey(key), value)
            else -> throw IllegalArgumentException("Unsupported data type: ${value!!::class.java.simpleName}")
        } as DataStoreValue<T>
    }
}