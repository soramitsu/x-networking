package jp.co.soramitsu.xnetworking.basic.txhistory.client.subsquid

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.BlockExplorerGraphQlRequest
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.client.TxHistoryClient
import jp.co.soramitsu.xnetworking.basic.txhistory.subsquid.graphqlrequest.txHistorySubSquidVariables
import kotlinx.serialization.DeserializationStrategy

class SubSquidClient<T, R>(
    private val networkClient: SoramitsuNetworkClient,
    private val pageSize: Int,
    private val deserializationStrategy: DeserializationStrategy<T>,
    private val jsonToHistoryInfo: (T) -> TxHistoryInfo,
    historyInfoToResult: (TxHistoryItem) -> R,
    private val historyRequest: String,
    historyDatabaseProvider: HistoryDatabaseProvider
) : TxHistoryClient<T, R>(pageSize, historyInfoToResult, historyDatabaseProvider) {

    companion object {
        private const val FIRST_PAGE_CURSOR = "1"
    }

    override suspend fun request(
        cursor: String?,
        address: String,
        url: String,
    ): TxHistoryInfo {
        val request = BlockExplorerGraphQlRequest(
            query = historyRequest,
            variables = txHistorySubSquidVariables(
                countRemote = pageSize,
                myAddress = address,
                cursor = cursor ?: FIRST_PAGE_CURSOR,
            )
        )
        val response = networkClient.createRequest<String>(
            url,
            HttpMethod.Post,
            request,
            ContentType.Application.Json
        )

        val decoded = networkClient.json.decodeFromString(deserializationStrategy, response)
        val d = jsonToHistoryInfo.invoke(decoded)
        hashCode()
        return d
    }
}
