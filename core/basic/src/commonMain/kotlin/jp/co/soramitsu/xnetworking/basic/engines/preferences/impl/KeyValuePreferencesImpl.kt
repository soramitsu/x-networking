package jp.co.soramitsu.xnetworking.basic.engines.preferences.impl

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import jp.co.soramitsu.xnetworking.basic.engines.preferences.api.KeyValuePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer

class KeyValuePreferencesImpl(
    private val settings: Settings
): KeyValuePreferences {

    override val dataFlow: Flow<Pair<String, Any>> = flow {
        settings.keys.map {
            it to settings.get<Any>(it)
        }
    }

    override suspend fun putString(field: String, value: String) {
        settings.putString(
            field, value
        )
    }

    override suspend fun getString(field: String): String =
        settings.getString(field, "")

    override suspend fun putLong(field: String, value: Long) {
        settings.putLong(field, value)
    }

    override suspend fun getLong(field: String, defaultValue: Long): Long =
        settings.getLong(field, defaultValue)

    override suspend fun putBoolean(field: String, value: Boolean) {
        settings.putBoolean(field, value)
    }

    override suspend fun getBoolean(field: String, defaultValue: Boolean): Boolean =
        settings.getBoolean(field, defaultValue)

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> putSerializable(serializer: KSerializer<T>, field: String, value: T) {
        settings.encodeValue(serializer, field, value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> getSerializable(serializer: KSerializer<T>, field: String): T? {
        return settings.decodeValueOrNull(serializer, field)
    }

    override suspend fun clear(field: String) {
        settings.remove(field)
    }

    override suspend fun clearAll() {
        settings.clear()
    }
}