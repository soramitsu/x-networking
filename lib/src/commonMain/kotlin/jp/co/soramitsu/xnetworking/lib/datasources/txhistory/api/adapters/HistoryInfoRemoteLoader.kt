package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

abstract class HistoryInfoRemoteLoader {

    @Throws(
        RestClientException::class,
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class
    )
    abstract suspend fun loadHistoryInfo(
        pageCount: Int,
        cursor: String?,
        signAddress: String,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryInfo

}