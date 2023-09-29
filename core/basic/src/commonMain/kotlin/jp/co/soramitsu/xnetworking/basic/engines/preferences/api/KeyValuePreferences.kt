package jp.co.soramitsu.xnetworking.basic.engines.preferences.api

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

interface KeyValuePreferences {

    val dataFlow: Flow<Pair<String, Any>>

    suspend fun putString(field: String, value: String)

    suspend fun getString(field: String): String

    suspend fun putLong(field: String, value: Long)

    suspend fun getLong(field: String, defaultValue: Long): Long

    suspend fun putBoolean(field: String, value: Boolean)

    suspend fun getBoolean(field: String, defaultValue: Boolean): Boolean

    suspend fun <T> putSerializable(serializer: KSerializer<T>, field: String, value: T)

    suspend fun <T> getSerializable(serializer: KSerializer<T>, field: String): T?

    suspend fun clear(field: String)

    suspend fun clearAll()
}