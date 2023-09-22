package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl

import io.ktor.client.call.body
import jp.co.soramitsu.xnetworking.basic.engines.apollo.impl.utils.getPrimitiveContentOrEmpty
import jp.co.soramitsu.xnetworking.basic.engines.apollo.impl.utils.asJsonObjectNullable
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.AbstractWhitelistedToken
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.WhitelistRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl.models.InternalGetRequest
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl.models.InternalWhitelistedToken
import kotlinx.serialization.json.JsonArray

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
                tokenAddress = it.asJsonObjectNullable.getPrimitiveContentOrEmpty("address"),
                rawIconLink = it.asJsonObjectNullable.getPrimitiveContentOrEmpty("icon"),
            )
        }
    }

}