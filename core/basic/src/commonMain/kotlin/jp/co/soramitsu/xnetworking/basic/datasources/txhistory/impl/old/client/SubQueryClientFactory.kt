package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.client

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import kotlinx.serialization.DeserializationStrategy

expect class SubQueryClientFactory<T, R> {
    fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        baseUrl: String,
        pageSize: Int,
        deserializationStrategy: DeserializationStrategy<T>,
        jsonToHistoryInfo: (T) -> TxHistoryInfo,
        historyIntoToResult: (TxHistoryItem) -> R,
        historyRequest: String,
    ): SubQueryClient<T, R>
}
