package jp.co.soramitsu.xnetworking.lib.engines.preferences.api

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

abstract class KeyValuePreferences {

    abstract val dataFlow: Flow<Pair<String, Any>>

    abstract suspend fun putString(field: String, value: String)

    abstract suspend fun getString(field: String): String

    abstract suspend fun putLong(field: String, value: Long)

    abstract suspend fun getLong(field: String, defaultValue: Long): Long

    abstract suspend fun putBoolean(field: String, value: Boolean)

    abstract suspend fun getBoolean(field: String, defaultValue: Boolean): Boolean

    abstract suspend fun <T> putSerializable(serializer: KSerializer<T>, field: String, value: T)

    abstract suspend fun <T> getSerializable(serializer: KSerializer<T>, field: String): T?

    abstract suspend fun clear(field: String)

    abstract suspend fun clearAll()
}