package jp.co.soramitsu.xnetworking.sorawallet.txhistory.client

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryResult
import jp.co.soramitsu.xnetworking.basic.txhistory.client.SubQueryClient
import jp.co.soramitsu.xnetworking.sorawallet.txhistory.subquery.soraHistoryGraphQLRequest
import jp.co.soramitsu.xnetworking.sorawallet.txhistory.subquery.models.SoraWalletSubQueryHistoryMapper
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraRemoteConfigBuilder
import jp.co.soramitsu.xnetworking.sorawallet.txhistory.subquery.models.SoraSubQueryResponse
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
            jsonToHistoryInfo = { response, address -> SoraWalletSubQueryHistoryMapper.map(response, address) },
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