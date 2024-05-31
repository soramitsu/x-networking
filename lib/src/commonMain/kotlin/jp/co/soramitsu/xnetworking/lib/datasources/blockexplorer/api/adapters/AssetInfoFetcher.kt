package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters

import com.apollographql.apollo3.exception.ApolloException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

abstract class AssetInfoFetcher {

    @Throws(
        ApolloException::class,
        RestClientException::class,
        CancellationException::class,
        IllegalStateException::class
    )
    abstract suspend fun fetch(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo>

}