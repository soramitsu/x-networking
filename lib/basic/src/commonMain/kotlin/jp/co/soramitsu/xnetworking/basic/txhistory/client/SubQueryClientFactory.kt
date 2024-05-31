package jp.co.soramitsu.xnetworking.basic.txhistory.client

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import kotlinx.serialization.DeserializationStrategy

expect class SubQueryClientFactory<T, R> {
    fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        baseUrl: String,
        pageSize: Int,
        deserializationStrategy: DeserializationStrategy<T>,
        jsonToHistoryInfo: (T, String) -> TxHistoryInfo,
        historyIntoToResult: (TxHistoryItem) -> R,
        historyRequest: String,
    ): SubQueryClient<T, R>
}
