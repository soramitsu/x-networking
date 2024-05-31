package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

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