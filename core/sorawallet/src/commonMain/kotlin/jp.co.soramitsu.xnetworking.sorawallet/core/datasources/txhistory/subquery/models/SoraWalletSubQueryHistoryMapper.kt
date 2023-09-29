package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.subquery.models

import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemParam
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object SoraWalletSubQueryHistoryMapper {
    fun map(response: SoraSubQueryResponse): TxHistoryInfo {
        return TxHistoryInfo(
            endCursor = response.data.historyElements.pageInfo.endCursor,
            endReached = response.data.historyElements.pageInfo.hasNextPage.not(),
            items = response.data.historyElements.nodes.map {
                TxHistoryItem(
                    id = it.id,
                    blockHash = it.blockHash,
                    module = it.module,
                    method = it.method,
                    timestamp = it.timestamp,
                    networkFee = it.networkFee,
                    success = it.execution.success,
                    data = (it.data as? JsonObject)?.let { json ->
                        json.map { mapItem ->
                            TxHistoryItemParam(
                                paramName = mapItem.key,
                                paramValue = (mapItem.value as? JsonPrimitive)?.content.orEmpty()
                            )
                        }
                    },
                    nestedData = (it.data as? JsonArray)?.let { jsonArray ->
                        jsonArray.filterIsInstance<JsonObject>().map { jsonObject ->
                            TxHistoryItemNested(
                                hash = (jsonObject["hash"] as? JsonPrimitive)?.content.orEmpty(),
                                module = (jsonObject["module"] as? JsonPrimitive)?.content.orEmpty(),
                                method = (jsonObject["method"] as? JsonPrimitive)?.content.orEmpty(),
                                data = ((jsonObject["data"] as? JsonObject)?.get("args") as? JsonObject)?.map { mapItem ->
                                    TxHistoryItemParam(
                                        paramName = mapItem.key,
                                        paramValue = (mapItem.value as? JsonPrimitive)?.content.orEmpty()
                                    )
                                } ?: emptyList()
                            )
                        }
                    },
                )
            },
        )
    }
}
