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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * @author: wuc
 * @date: 2024/10/23
 * @description: DataStore 工具类封装
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "datastore")

object DataStoreUtils {

    fun <T> get(key: String, defaultValue: T): T {
        return runBlocking { asyncGetPreference(key, defaultValue) }
    }

    fun <T> put(key: String, value: T) {
        runBlocking { asyncSetPreference(key, value) }
    }

    /**
     * 保存数据
     * */
    private suspend fun <T> asyncSetPreference(key: String, value: T) {
        application.dataStore.edit { setting ->
            when (value) {
                is Int -> setting[intPreferencesKey(key)] = value
                is Long -> setting[longPreferencesKey(key)] = value
                is Double -> setting[doublePreferencesKey(key)] = value
                is Float -> setting[floatPreferencesKey(key)] = value
                is Boolean -> setting[booleanPreferencesKey(key)] = value
                is String -> setting[stringPreferencesKey(key)] = value
                is ByteArray -> setting[byteArrayPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("This type can be saved into DataStore")
            }
        }
    }

    /**
     * 获取数据
     * */
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> asyncGetPreference(key: String, defaultValue: T): T {
        return application.dataStore.data.map { setting ->
            when (defaultValue) {
                is Int-> setting[intPreferencesKey(key)] ?: defaultValue
                is Long -> setting[longPreferencesKey(key)] ?: defaultValue
                is Double -> setting[doublePreferencesKey(key)] ?: defaultValue
                is Float -> setting[floatPreferencesKey(key)] ?: defaultValue
                is Boolean -> setting[booleanPreferencesKey(key)] ?: defaultValue
                is String -> setting[stringPreferencesKey(key)] ?: defaultValue
                is ByteArray -> setting[byteArrayPreferencesKey(key)] ?: defaultValue
                else -> throw IllegalArgumentException("This type can be get into DataStore")
            }
        }.first() as T

    }
}