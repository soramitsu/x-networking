package jp.co.soramitsu.xnetworking.basic.txhistory.client.subquery

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.BlockExplorerGraphQlRequest
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.client.TxHistoryClient
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.txHistorySubQueryVariables
import kotlinx.serialization.DeserializationStrategy

class SubQueryClient<T, R>(
    private val networkClient: SoramitsuNetworkClient,
    private val pageSize: Int,
    private val deserializationStrategy: DeserializationStrategy<T>,
    private val jsonToHistoryInfo: (T) -> TxHistoryInfo,
    historyInfoToResult: (TxHistoryItem) -> R,
    private val historyRequest: String,
    historyDatabaseProvider: HistoryDatabaseProvider
) : TxHistoryClient<T, R>(pageSize, historyInfoToResult, historyDatabaseProvider) {

    override suspend fun request(
        cursor: String?,
        address: String,
        url: String,
    ): TxHistoryInfo {
        val request = BlockExplorerGraphQlRequest(
            query = historyRequest,
            variables = txHistorySubQueryVariables(
                countRemote = pageSize,
                myAddress = address,
                cursor = cursor.orEmpty(),
            )
        )
        val response = networkClient.createRequest<String>(
            url,
            HttpMethod.Post,
            request,
            ContentType.Application.Json
        )
        val decoded = networkClient.json.decodeFromString(deserializationStrategy, response)
        return jsonToHistoryInfo.invoke(decoded)
    }
}
