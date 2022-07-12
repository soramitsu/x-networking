package jp.co.soramitsu.commonnetworking.subquery.factory

import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.commonnetworking.subquery.SubQueryClient
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryInfo
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItem
import kotlinx.serialization.DeserializationStrategy

expect class SubQueryClientFactory<T, R> {
    fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        baseUrl: String,
        pageSize: Int,
        deserializationStrategy: DeserializationStrategy<T>,
        jsonToHistoryInfo: (T) -> SubQueryHistoryInfo,
        historyIntoToResult: (SubQueryHistoryItem) -> R,
        historyRequest: String,
    ): SubQueryClient<T, R>
}
