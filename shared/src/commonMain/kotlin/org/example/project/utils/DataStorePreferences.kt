package org.example.project.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataStorePreferences(val dataStore: DataStore<Preferences>) {

    val data: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    // Generic type-safe getters & setters
    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T? = null): Flow<T?> {
        return data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    // String Key Value
    suspend fun saveString(key: String, value: String) {
        savePreference(stringPreferencesKey(key), value)
    }

    fun getString(key: String, defaultValue: String? = null): Flow<String?> {
        return getPreference(stringPreferencesKey(key), defaultValue)
    }

    // Boolean Key Value
    suspend fun saveBoolean(key: String, value: Boolean) {
        savePreference(booleanPreferencesKey(key), value)
    }

    fun getBoolean(key: String, defaultValue: Boolean? = null): Flow<Boolean?> {
        return getPreference(booleanPreferencesKey(key), defaultValue)
    }

    // Int Key Value
    suspend fun saveInt(key: String, value: Int) {
        savePreference(intPreferencesKey(key), value)
    }

    fun getInt(key: String, defaultValue: Int? = null): Flow<Int?> {
        return getPreference(intPreferencesKey(key), defaultValue)
    }

    // Long Key Value
    suspend fun saveLong(key: String, value: Long) {
        savePreference(longPreferencesKey(key), value)
    }

    fun getLong(key: String, defaultValue: Long? = null): Flow<Long?> {
        return getPreference(longPreferencesKey(key), defaultValue)
    }

    // Float Key Value
    suspend fun saveFloat(key: String, value: Float) {
        savePreference(floatPreferencesKey(key), value)
    }

    fun getFloat(key: String, defaultValue: Float? = null): Flow<Float?> {
        return getPreference(floatPreferencesKey(key), defaultValue)
    }

    // Double Key Value
    suspend fun saveDouble(key: String, value: Double) {
        savePreference(doublePreferencesKey(key), value)
    }

    fun getDouble(key: String, defaultValue: Double? = null): Flow<Double?> {
        return getPreference(doublePreferencesKey(key), defaultValue)
    }

    // App specific shortcuts
    val isFirstTime: Flow<Boolean> = data.map { preferences ->
        preferences[PreferencesKeys.KEY_IS_FIRST_TIME] ?: true
    }

    suspend fun setIsFirstTime(isFirstTime: Boolean) {
        savePreference(PreferencesKeys.KEY_IS_FIRST_TIME, isFirstTime)
    }

    val userName: Flow<String?> = data.map { preferences ->
        preferences[PreferencesKeys.KEY_USER_NAME]
    }

    suspend fun saveUserName(name: String) {
        savePreference(PreferencesKeys.KEY_USER_NAME, name)
    }

    // JSON Serialization Helpers for Complex Models
    suspend inline fun <reified T> saveObject(key: String, value: T, json: Json = Json { ignoreUnknownKeys = true }) {
        val serialized = json.encodeToString(value)
        saveString(key, serialized)
    }

    inline fun <reified T> getObject(key: String, json: Json = Json { ignoreUnknownKeys = true }): Flow<T?> {
        return getString(key).map { serialized ->
            serialized?.let {
                try {
                    json.decodeFromString<T>(it)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    // Clear all preferences (e.g. on user logout)
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
