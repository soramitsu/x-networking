package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl

import io.ktor.client.call.body
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.AbstractWhitelistedToken
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.WhitelistRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl.models.InternalGetRequest
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl.models.InternalWhitelistedToken
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class WhitelistRepositoryImpl(
    private val restClient: RestClient
): WhitelistRepository {

    override suspend fun getWhitelistedTokens(requestUrl: String): List<AbstractWhitelistedToken> {
        val responseAsJsonArray = restClient.get(
            request = InternalGetRequest(
                requestUrl = requestUrl
            )
        ).body<JsonArray>()

        return responseAsJsonArray.map {
            InternalWhitelistedToken(
                tokenAddress = (it as? JsonObject)?.get("address")
                    ?.jsonPrimitive?.content.orEmpty(),
                rawIconLink = (it as? JsonObject)?.get("icon")
                    ?.jsonPrimitive?.content.orEmpty(),
            )
        }
    }

}