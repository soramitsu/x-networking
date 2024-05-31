package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api

import com.apollographql.apollo3.exception.ApolloException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.wrappers.TxHistoryResult
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

abstract class TxHistoryRepository {

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    abstract fun getTransactionPeers(
        query: String,
        chainId: String
    ): List<String>

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    abstract fun getTransactionHistoryCached(
        count: Int,
        address: String,
        chainId: String,
    ): List<TxHistoryItem>

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    abstract fun getTransactionCached(
        txHash: String,
        address: String,
        chainId: String,
    ): TxHistoryInfo

    @Throws(
        ApolloException::class,
        RestClientException::class,
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class
    )
    abstract suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
        pageCount: Int,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryResult<TxHistoryItem>

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    abstract fun clearData(
        address: String,
        chainId: String
    )

    abstract fun clearAllData()

}