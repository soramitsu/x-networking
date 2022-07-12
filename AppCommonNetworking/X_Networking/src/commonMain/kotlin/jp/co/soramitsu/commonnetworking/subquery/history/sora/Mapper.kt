package jp.co.soramitsu.commonnetworking.subquery.history.sora

import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryInfo
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItem
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItemNested
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItemParam
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal object SoraMapper {
    fun map(response: SoraSubqueryResponse): SubQueryHistoryInfo {
        return SubQueryHistoryInfo(
            endCursor = response.data.historyElements.pageInfo.endCursor,
            endReached = response.data.historyElements.pageInfo.hasNextPage.not(),
            items = response.data.historyElements.nodes.map {
                SubQueryHistoryItem(
                    id = it.id,
                    blockHash = it.blockHash,
                    module = it.module,
                    method = it.method,
                    timestamp = it.timestamp,
                    networkFee = it.networkFee,
                    success = it.execution.success,
                    data = (it.data as? JsonObject)?.let { json ->
                        json.map { mapItem ->
                            SubQueryHistoryItemParam(
                                paramName = mapItem.key,
                                paramValue = (mapItem.value as? JsonPrimitive)?.content.orEmpty()
                            )
                        }
                    },
                    nestedData = (it.data as? JsonArray)?.let { jsonArray ->
                        jsonArray.filterIsInstance<JsonObject>().map { jsonObject ->
                            SubQueryHistoryItemNested(
                                hash = (jsonObject["hash"] as? JsonPrimitive)?.content.orEmpty(),
                                module = (jsonObject["module"] as? JsonPrimitive)?.content.orEmpty(),
                                method = (jsonObject["method"] as? JsonPrimitive)?.content.orEmpty(),
                                data = ((jsonObject["data"] as? JsonObject)?.get("args") as? JsonObject)?.map { mapItem ->
                                    SubQueryHistoryItemParam(
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
