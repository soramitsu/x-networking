package jp.co.soramitsu.xnetworking.fearlesswallet.common.interactors.txhistory.api

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
        networkName: String,
    ): List<String>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryCached(
        count: Int,
        address: String,
        networkName: String,
    ): List<TxHistoryItem>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionCached(
        txHash: String,
        address: String,
        networkName: String,
    ): TxHistoryInfo

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
        requestUrl: String,
        networkName: String,
    ): TxHistoryResult<TxHistoryItem>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun clearData(
        address: String,
        networkName: String,
    )

    suspend fun clearAllData()

}