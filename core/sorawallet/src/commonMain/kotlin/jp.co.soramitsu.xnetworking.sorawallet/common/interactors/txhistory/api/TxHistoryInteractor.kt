package jp.co.soramitsu.xnetworking.sorawallet.common.interactors.txhistory.api

import com.apollographql.apollo3.exception.ApolloException
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers.TxHistoryResult
import kotlin.coroutines.cancellation.CancellationException

interface TxHistoryInteractor {

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionPeers(
        query: String,
    ): List<String>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryCached(
        count: Int,
        address: String,
    ): List<TxHistoryItem>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionCached(
        txHash: String,
        address: String,
    ): TxHistoryInfo

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
    ): TxHistoryResult<TxHistoryItem>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun clearData(
        address: String,
    )

    suspend fun clearAllData()

}