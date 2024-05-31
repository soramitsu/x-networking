package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigFetcher
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequest
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class InMemorySavingConfigFetcherImpl(
    private val restClient: RestClient,
    private val chainsRequestUrl: String
): ConfigFetcher() {

    private val cachedValueReadWriteMutex: Mutex = Mutex()
    private var cachedValue: Map<String, JsonObject>? = null

    override suspend fun fetch(chainId: String): JsonObject {
        // Pattern: double-checked locking for singletons
        if (cachedValue == null) {
            cachedValueReadWriteMutex.withLock {
                if (cachedValue == null)
                    cachedValue = fetchConfig()
            }
        }
        return requireNotNull(
            cachedValue?.get(chainId)
        ) { "Tried to fetch config for chain with id - $chainId - but it was missing." }
    }

    private suspend fun fetchConfig(): Map<String, JsonObject> {
        return restClient.get(
            request = JsonGetRequest(
                url = chainsRequestUrl,
                responseDeserializer = JsonArray.serializer()
            )
        ).mapNotNull { element ->
            if (element !is JsonObject)
                return@mapNotNull null

            element.fieldOrNull("chainId").orEmpty() to element
        }.toMap()
    }

}