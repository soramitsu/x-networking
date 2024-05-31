package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException

/**
 * This is an abstract config fetcher since it returns JsonObject without
 * it being deserialized, due to the fact that different configs have different
 * representations.
 *
 * It is a publicly available for reason: to make mocking process easier
 */
abstract class ConfigFetcher {
    @Throws(
        RestClientException::class,
        CancellationException::class
    )
    abstract suspend fun fetch(chainId: String): JsonObject
}

