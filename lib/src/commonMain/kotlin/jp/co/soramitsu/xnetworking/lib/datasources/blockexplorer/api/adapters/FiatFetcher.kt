package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

abstract class FiatFetcher {

    @Throws(
        RestClientException::class,
        CancellationException::class,
        IllegalStateException::class
    )
    abstract suspend fun fetch(
        chainId: String
    ): List<Fiat>

}