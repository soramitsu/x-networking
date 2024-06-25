package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigParser
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

class JsonConfigParserImpl : ConfigParser() {

    private val cachedValueReadWriteMutex: Mutex = Mutex()
    private var cachedValue: Map<String, JsonObject>? = null

    private var jsonElement: JsonElement? = null

    suspend fun replaceJson(json: JsonElement) {
        cachedValueReadWriteMutex.withLock {
            cachedValue = null
            jsonElement = json
        }
    }

    override suspend fun getChainObjectById(chainId: String): JsonObject {
        // Pattern: double-checked locking for singletons
        if (cachedValue == null) {
            cachedValueReadWriteMutex.withLock {
                if (cachedValue == null)
                    cachedValue = jsonElement?.jsonArray?.mapNotNull { element ->
                        if (element !is JsonObject)
                            return@mapNotNull null

                        element.fieldOrNull("chainId").orEmpty() to element
                    }?.toMap()
            }
        }
        return requireNotNull(
            cachedValue?.get(chainId)
        ) { "Tried to parse config for chain with id - $chainId - but it was missing." }
    }
}