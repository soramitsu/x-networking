package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigParser
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class StringConfigParserImpl(
    private val json: Json
) : ConfigParser() {

    private val cachedValueReadWriteMutex: Mutex = Mutex()
    private var cachedValue: Map<String, JsonObject>? = null

    private var string: String? = null

    private var hasStringChanged: Boolean = false

    suspend fun replaceStringJson(value: String) {
        cachedValueReadWriteMutex.withLock {
            cachedValue = null
            string = value
            hasStringChanged = true
        }
    }

    override suspend fun getChainObjectById(chainId: String): JsonObject {
        // Pattern: double-checked locking for singletons
        if (cachedValue == null || hasStringChanged) {
            cachedValueReadWriteMutex.withLock {
                if (cachedValue == null || hasStringChanged) {
                    cachedValue = string?.let { value ->
                        json.decodeFromString(
                            string = value,
                            deserializer = JsonArray.serializer()
                        ).mapNotNull { element ->
                            if (element !is JsonObject)
                                return@mapNotNull null

                            element.fieldOrNull("chainId").orEmpty() to element
                        }.toMap()
                    }

                    hasStringChanged = false
                }
            }
        }
        return requireNotNull(
            cachedValue?.get(chainId)
        ) { "Tried to parse config for chain with id - $chainId - but it was missing." }
    }
}