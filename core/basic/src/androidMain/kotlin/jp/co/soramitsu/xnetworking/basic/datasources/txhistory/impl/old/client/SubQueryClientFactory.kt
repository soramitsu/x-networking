package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.client

import android.content.Context
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import kotlinx.serialization.DeserializationStrategy

actual class SubQueryClientFactory<T, R>(private val context: Context) {
    actual fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        baseUrl: String,
        pageSize: Int,
        deserializationStrategy: DeserializationStrategy<T>,
        jsonToHistoryInfo: (T) -> TxHistoryInfo,
        historyIntoToResult: (TxHistoryItem) -> R,
        historyRequest: String,
    ): SubQueryClient<T, R> {
        return SubQueryClient(
            soramitsuNetworkClient,
            pageSize,
            deserializationStrategy,
            jsonToHistoryInfo,
            historyIntoToResult,
            historyRequest,
            HistoryDatabaseProvider(DatabaseDriverFactory(context))
        )
    }
}
