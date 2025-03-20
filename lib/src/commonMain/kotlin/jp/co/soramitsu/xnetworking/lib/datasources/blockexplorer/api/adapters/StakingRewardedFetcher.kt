package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters

import com.apollographql.apollo.exception.ApolloException
import io.mockative.Mockable
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException
@Mockable
abstract class StakingRewardedFetcher {

    @Throws(
        ApolloException::class,
        RestClientException::class,
        CancellationException::class,
        IllegalStateException::class,
    )
    abstract suspend fun fetch(
        chainId: String,
        address: String,
    ): List<String>
}
