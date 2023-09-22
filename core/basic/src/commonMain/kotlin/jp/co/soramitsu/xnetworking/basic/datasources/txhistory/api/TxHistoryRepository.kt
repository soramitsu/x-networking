package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers.TxHistoryResult
import kotlin.coroutines.cancellation.CancellationException

interface TxHistoryRepository {

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    fun getTransactionPeers(
        query: String,
        networkName: String
    ): List<String>

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    fun getTransactionHistoryCached(
        count: Int,
        address: String,
        networkName: String,
    ): List<TxHistoryItem>

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    fun getTransactionCached(
        txHash: String,
        address: String,
        networkName: String,
    ): TxHistoryInfo

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
        networkName: String,
    ): TxHistoryResult<TxHistoryItem>

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    fun clearData(
        address: String,
        networkName: String
    )

    fun clearAllData()

}