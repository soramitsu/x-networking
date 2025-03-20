package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters

import io.mockative.Mockable
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

@Mockable
abstract class ApyFetcher {

    @Throws(
        RestClientException::class,
        CancellationException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class
    )
    abstract suspend fun fetch(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy>

}