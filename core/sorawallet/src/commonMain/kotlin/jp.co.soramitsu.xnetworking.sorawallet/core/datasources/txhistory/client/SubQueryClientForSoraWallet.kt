package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.client

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers.TxHistoryResult
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.client.SubQueryClient
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.subquery.soraHistoryGraphQLRequest
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.subquery.models.SoraWalletSubQueryHistoryMapper
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraRemoteConfigBuilder
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.subquery.models.SoraSubQueryResponse
import kotlin.coroutines.cancellation.CancellationException

class SubQueryClientForSoraWallet(
    networkClient: SoramitsuNetworkClient,
    pageSize: Int,
    historyDatabaseProvider: HistoryDatabaseProvider,
    private val soraRemoteConfigBuilder: SoraRemoteConfigBuilder,
) {

    private val networkName = "SORA"

    private val client: SubQueryClient<SoraSubQueryResponse, TxHistoryItem> =
        SubQueryClient(
            networkClient = networkClient,
            pageSize = pageSize,
            deserializationStrategy = SoraSubQueryResponse.serializer(),
            jsonToHistoryInfo = { response -> SoraWalletSubQueryHistoryMapper.map(response) },
            historyInfoToResult = { it },
            historyRequest = soraHistoryGraphQLRequest(),
            historyDatabaseProvider = historyDatabaseProvider,
        )

    fun clearAllData() = client.clearAllData()

    fun clearData(address: String) =
        client.clearData(address, networkName)

    fun getTransactionPeers(query: String): List<String> =
        client.getTransactionPeers(query, networkName)

    fun getTransactionHistoryCached(
        address: String,
        count: Int,
        filter: ((TxHistoryItem) -> Boolean)? = null,
    ): List<TxHistoryItem> = client.getTransactionHistoryCached(address, networkName, count, filter)

    fun getTransactionCached(
        address: String,
        txHash: String,
    ): TxHistoryInfo = client.getTransactionCached(address, networkName, txHash)

    @Throws(
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
        filter: ((TxHistoryItem) -> Boolean)? = null
    ): TxHistoryResult<TxHistoryItem>? {
        val explorerUrl = soraRemoteConfigBuilder.getConfig()?.blockExplorerUrl ?: return null
        return client.getTransactionHistoryPaged(
            address, networkName, page, explorerUrl, filter
        )
    }
}